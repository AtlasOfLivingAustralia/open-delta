/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives.invocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.rtf.RTFWriter;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class DescribeDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<File> {

    private List<Item> _taxa;
    private boolean _includeSpecimen;
    private List<Character> _characters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        this._includeSpecimen = pair.getSecond();
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public File doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        IntkeyDataset dataset = context.getDataset();

        Specimen specimen = context.getSpecimen();
        Map<Character, String> charactersItemSubheadingMap = generateCharactersItemSubheadingMap(dataset);

        // Similarities output can be very large so write it to a temporary
        // file.
        try {
            File tempFile = File.createTempFile("IntkeyDescribe", null);
            tempFile.deleteOnExit();
            FileWriter fw = new FileWriter(tempFile);
            RTFWriter rtfWriter = new RTFWriter(fw);

            if (dataset.itemSubheadingsPresent() && !context.displayNumbering()) {
                generateReportGroupedByItemSubheading(context.getDataset(), specimen, charactersItemSubheadingMap, context.displayUnknowns(), context.displayInapplicables(), dataset.getOrWord(), rtfWriter);
            } else {
                generateStandardReport(context.getDataset(), specimen, charactersItemSubheadingMap, context.displayNumbering(), context.displayUnknowns(), context.displayInapplicables(),
                        dataset.getOrWord(), rtfWriter);
            }

            return tempFile;
        } catch (IOException ex) {
            throw new IntkeyDirectiveInvocationException(ex, "Error generating describe report: %s.", ex.getMessage());
        }
    }

    @Override
    protected void handleProcessingDone(IntkeyContext context, File result) {
        context.getUI().displayRTFReportFromFile(result, UIUtils.getResourceString("DescribeDirective.ReportTitle"));
    }

    // Generate a desciption with each attribute value listed on a separate line
    private void generateStandardReport(IntkeyDataset dataset, Specimen specimen, Map<Character, String> charactersItemSubheadingMap, boolean displayNumbering,
            boolean displayInapplicables, boolean displayUnknowns, String orWord, RTFWriter writer) throws IOException {
        ItemFormatter taxonFormatter = new ItemFormatter(displayNumbering, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        CharacterFormatter characterFormatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(displayNumbering, false, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, orWord);

        writer.startDocument();
        
        int numTaxaProcessed = 0;
        updateProgess(numTaxaProcessed, _taxa.size());

        if (_includeSpecimen) {
            writer.writeText("Specimen");
            writer.increaseIndent();

            String currentItemSubheading = null;

            for (Character ch : _characters) {
                Attribute attr = specimen.getAttributeForCharacter(ch);
                currentItemSubheading = standardReportHandleAttribute(writer, attr, charactersItemSubheadingMap, currentItemSubheading, displayInapplicables, displayUnknowns, characterFormatter,
                        attributeFormatter);
            }

            writer.decreaseIndent();
            updateProgess(++numTaxaProcessed, _taxa.size());
        }

        for (Item taxon : _taxa) {
            writer.writeText(taxonFormatter.formatItemDescription(taxon));
            writer.increaseIndent();

            String currentItemSubheading = null;

            for (Character ch : _characters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());
                currentItemSubheading = standardReportHandleAttribute(writer, attr, charactersItemSubheadingMap, currentItemSubheading, displayInapplicables, displayUnknowns, characterFormatter,
                        attributeFormatter);
            }

            writer.writeText("");
            writer.decreaseIndent();
            updateProgess(++numTaxaProcessed, _taxa.size());
        }

        writer.endDocument();
    }

    // Helper method for generateStandardReport()
    private String standardReportHandleAttribute(RTFWriter writer, Attribute attr, Map<Character, String> charactersItemSubheadingMap, String currentItemSubheading, boolean displayInapplicables,
            boolean displayUnknowns, CharacterFormatter characterFormatter, AttributeFormatter attributeFormatter) throws IOException {

        Character ch = attr.getCharacter();

        if ((!(attr.isInapplicable() && attr.isUnknown()) || displayInapplicables) && (!attr.isUnknown() || displayUnknowns)) {
            String itemSubheading = charactersItemSubheadingMap.get(ch);
            if (itemSubheading != null && !itemSubheading.equals(currentItemSubheading)) {

                // For some reason the DELTA sample data set contains an item
                // subheading that is an empty
                // paragraph. Ignore any item subheadings that contain only
                // formatting marks
                if (!StringUtils.isEmpty(RTFUtils.stripFormatting(itemSubheading))) {
                    writer.writeText(itemSubheading);
                }

                currentItemSubheading = itemSubheading;
            }

            String characterDescription = characterFormatter.formatCharacterDescription(ch);
            String attributeDescription = attributeFormatter.formatAttribute(attr);

            StringBuilder lineToInsert = new StringBuilder();
            lineToInsert.append(characterDescription);
            lineToInsert.append(" ");
            lineToInsert.append(attributeDescription);
            if (!ch.getOmitPeriod()) {
                lineToInsert.append(".");
            }

            writer.writeText(lineToInsert.toString());
        }

        return currentItemSubheading;
    }

    // Generate a description with the attribute values grouped by item
    // subheadings in paragraphs
    private void generateReportGroupedByItemSubheading(IntkeyDataset dataset, Specimen specimen, Map<Character, String> charactersItemSubheadingMap,
            boolean displayInapplicables, boolean displayUnknowns, String orWord, RTFWriter writer) throws IOException {
        ItemFormatter taxonFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(false, false, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, orWord);

        writer.startDocument();
        
        int numTaxaProcessed = 0;
        updateProgess(numTaxaProcessed, _taxa.size());

        if (_includeSpecimen) {
            writer.writeText("Specimen");
            writer.increaseIndent();

            String currentItemSubheading = null;
            StringBuilder itemSubheadingGroupBuilder = new StringBuilder();

            for (Character ch : _characters) {
                Attribute attr = specimen.getAttributeForCharacter(ch);
                currentItemSubheading = groupedByItemSubheadingReportHandleAttribute(writer, itemSubheadingGroupBuilder, attr, charactersItemSubheadingMap, currentItemSubheading,
                        displayInapplicables, displayUnknowns, characterFormatter, attributeFormatter);
            }

            if (itemSubheadingGroupBuilder.length() > 0) {
                writer.writeText(itemSubheadingGroupBuilder.toString());
            }

            writer.decreaseIndent();
            updateProgess(++numTaxaProcessed, _taxa.size());
        }

        for (Item taxon : _taxa) {
            writer.writeText(taxonFormatter.formatItemDescription(taxon));
            writer.increaseIndent();

            String currentItemSubheading = null;
            StringBuilder itemSubheadingGroupBuilder = new StringBuilder();

            for (Character ch : _characters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());
                currentItemSubheading = groupedByItemSubheadingReportHandleAttribute(writer, itemSubheadingGroupBuilder, attr, charactersItemSubheadingMap, currentItemSubheading,
                        displayInapplicables, displayUnknowns, characterFormatter, attributeFormatter);

            }

            if (itemSubheadingGroupBuilder.length() > 0) {
                writer.writeText(itemSubheadingGroupBuilder.toString());
            }

            writer.writeText("");
            writer.decreaseIndent();
            updateProgess(++numTaxaProcessed, _taxa.size());
        }

        writer.endDocument();
    }

    // Helper method for generateReportGroupedByItemSubheading()
    private String groupedByItemSubheadingReportHandleAttribute(RTFWriter writer, StringBuilder itemSubheadingGroupBuilder, Attribute attr, Map<Character, String> charactersItemSubheadingMap,
            String currentItemSubheading, boolean displayInapplicables, boolean displayUnknowns, CharacterFormatter characterFormatter, AttributeFormatter attributeFormatter) throws IOException {

        Character ch = attr.getCharacter();

        if ((!(attr.isInapplicable() && attr.isUnknown()) || displayInapplicables) && (!attr.isUnknown() || displayUnknowns)) {

            // If this is the beginning of a new itemsubheading category then we
            // need to add the item subheading
            String itemSubheading = charactersItemSubheadingMap.get(ch);
            if (itemSubheading != null && !itemSubheading.equals(currentItemSubheading)) {
                if (itemSubheadingGroupBuilder.length() > 0) {
                    writer.writeText(itemSubheadingGroupBuilder.toString());
                }
                currentItemSubheading = itemSubheading;

                // Can't create a new StringBuilder here as the calling method
                // maintains a reference to the old one and keeps using it
                // so just set the length to zero to clear it instead.
                itemSubheadingGroupBuilder.setLength(0);

                itemSubheadingGroupBuilder.append(currentItemSubheading);
                itemSubheadingGroupBuilder.append(" ");
            } else if (ch.getNewParagraph()) {
                // Add a paragraph break
                if (itemSubheadingGroupBuilder.length() > 0) {
                    itemSubheadingGroupBuilder.append(" \\par ");
                }
            }

            String characterDescription = characterFormatter.formatCharacterDescription(ch);
            String attributeDescription = attributeFormatter.formatAttribute(attr);

            if (!StringUtils.isEmpty(characterDescription)) {
                itemSubheadingGroupBuilder.append(characterDescription);
                itemSubheadingGroupBuilder.append(" ");
                itemSubheadingGroupBuilder.append(attributeDescription);
            } else {
                attributeDescription = Utils.capitaliseFirstWord(attributeDescription);
                itemSubheadingGroupBuilder.append(attributeDescription);
            }

            if (!ch.getOmitPeriod()) {
                itemSubheadingGroupBuilder.append(".");
            }

            itemSubheadingGroupBuilder.append(" ");

        }

        return currentItemSubheading;
    }

    // Build a dictionary of item subheadings keyed by character.
    private Map<Character, String> generateCharactersItemSubheadingMap(IntkeyDataset ds) {
        if (!ds.itemSubheadingsPresent()) {
            return Collections.EMPTY_MAP;
        }

        Map<Character, String> retMap = new HashMap<Character, String>();

        String currentSubHeading = null;

        for (Character ch : ds.getCharactersAsList()) {
            if (ch.getItemSubheading() != null) {
                currentSubHeading = ch.getItemSubheading();
            }

            retMap.put(ch, currentSubHeading);
        }

        return retMap;
    }

    // Build dictionary of attributes for all taxa keyed by the corresponding
    // character
    private Map<Character, List<Attribute>> generateCharacterAttributesMap(IntkeyDataset dataset) {
        Map<Character, List<Attribute>> characterAttributesMap = new HashMap<Character, List<Attribute>>();

        for (Character ch : _characters) {
            List<Attribute> attrs = dataset.getAllAttributesForCharacter(ch.getCharacterId());
            characterAttributesMap.put(ch, attrs);
        }

        return characterAttributesMap;
    }
    
    private void updateProgess(int numTaxaProcessed, int totalNumTaxa) {
        int progressPercent = (int) Math.floor((((double) numTaxaProcessed) / totalNumTaxa) * 100);
        progress(UIUtils.getResourceString("DescribeDirective.Progress.Generating", progressPercent));
    }

}
