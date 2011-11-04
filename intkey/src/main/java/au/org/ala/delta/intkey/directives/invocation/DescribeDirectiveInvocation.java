package au.org.ala.delta.intkey.directives.invocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class DescribeDirectiveInvocation extends IntkeyDirectiveInvocation {

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
    public boolean execute(IntkeyContext context) {
        IntkeyDataset dataset = context.getDataset();
        
        Specimen specimen = context.getSpecimen();
        Map<Character, List<Attribute>> characterAttributesMap = generateCharacterAttributesMap(dataset);
        Map<Character, String> charactersItemSubheadingMap = generateCharactersItemSubheadingMap(dataset);

        RTFBuilder builder = null;
        if (dataset.itemSubheadingsPresent() && !context.displayNumbering()) {
            builder = generateReportGroupedByItemSubheading(characterAttributesMap, specimen, charactersItemSubheadingMap, context.displayUnknowns(), context.displayInapplicables(),
                    dataset.getOrWord());
        } else {
            builder = generateStandardReport(characterAttributesMap, specimen, charactersItemSubheadingMap, context.displayNumbering(), context.displayUnknowns(), context.displayInapplicables(),
                    dataset.getOrWord());
        }

        context.getUI().displayRTFReport(builder.toString(), "Describe");

        return true;
    }

    // Generate a desciption with each attribute value listed on a separate line
    private RTFBuilder generateStandardReport(Map<Character, List<Attribute>> characterAttributesMap, Specimen specimen, Map<Character, String> charactersItemSubheadingMap, boolean displayNumbering,
            boolean displayInapplicables, boolean displayUnknowns, String orWord) {
        ItemFormatter taxonFormatter = new ItemFormatter(displayNumbering, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        CharacterFormatter characterFormatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(displayNumbering, false, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, orWord);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        if (_includeSpecimen) {
            builder.appendText("Specimen");
            builder.increaseIndent();

            String currentItemSubheading = null;

            for (Character ch : _characters) {
                // TODO need to refactor specimen class to take attribute
                // values directly.
                Attribute attr = DiffUtils.createAttributeForSpecimenValue(specimen, ch);
                currentItemSubheading = standardReportHandleAttribute(builder, attr, charactersItemSubheadingMap, currentItemSubheading, displayInapplicables, displayUnknowns, characterFormatter,
                        attributeFormatter);
            }

            builder.decreaseIndent();
        }

        for (Item taxon : _taxa) {
            builder.appendText(taxonFormatter.formatItemDescription(taxon));
            builder.increaseIndent();

            String currentItemSubheading = null;

            for (Character ch : _characters) {
                Attribute attr = characterAttributesMap.get(ch).get(taxon.getItemNumber() - 1);
                currentItemSubheading = standardReportHandleAttribute(builder, attr, charactersItemSubheadingMap, currentItemSubheading, displayInapplicables, displayUnknowns, characterFormatter,
                        attributeFormatter);
            }

            builder.appendText("");
            builder.decreaseIndent();
        }

        builder.endDocument();

        return builder;
    }

    // Helper method for generateStandardReport()
    private String standardReportHandleAttribute(RTFBuilder builder, Attribute attr, Map<Character, String> charactersItemSubheadingMap, String currentItemSubheading, boolean displayInapplicables,
            boolean displayUnknowns, CharacterFormatter characterFormatter, AttributeFormatter attributeFormatter) {

        Character ch = attr.getCharacter();

        if ((!attr.isInapplicable() || displayInapplicables) && (!attr.isUnknown() || displayUnknowns)) {
            String itemSubheading = charactersItemSubheadingMap.get(ch);
            if (itemSubheading != null && !itemSubheading.equals(currentItemSubheading)) {

                // For some reason the DELTA sample data set contains an item
                // subheading that is an empty
                // paragraph. Ignore any item subheadings that contain only
                // formatting marks
                if (!StringUtils.isEmpty(RTFUtils.stripFormatting(currentItemSubheading))) {
                    builder.appendText(itemSubheading);
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

            builder.appendText(lineToInsert.toString());
        }

        return currentItemSubheading;
    }

    // Generate a description with the attribute values grouped by item
    // subheadings in paragraphs
    private RTFBuilder generateReportGroupedByItemSubheading(Map<Character, List<Attribute>> characterAttributesMap, Specimen specimen, Map<Character, String> charactersItemSubheadingMap,
            boolean displayInapplicables, boolean displayUnknowns, String orWord) {
        ItemFormatter taxonFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(false, false, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, orWord);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        if (_includeSpecimen) {
            builder.appendText("Specimen");
            builder.increaseIndent();

            String currentItemSubheading = null;
            StringBuilder itemSubheadingGroupBuilder = new StringBuilder();

            for (Character ch : _characters) {
                // TODO need to refactor specimen class to take attribute
                // values directly.
                Attribute attr = DiffUtils.createAttributeForSpecimenValue(specimen, ch);
                currentItemSubheading = groupedByItemSubheadingReportHandleAttribute(builder, itemSubheadingGroupBuilder, attr, charactersItemSubheadingMap, currentItemSubheading,
                        displayInapplicables, displayUnknowns, characterFormatter, attributeFormatter);
            }

            if (itemSubheadingGroupBuilder.length() > 0) {
                builder.appendText(itemSubheadingGroupBuilder.toString());
            }

            builder.decreaseIndent();
        }

        for (Item taxon : _taxa) {
            builder.appendText(taxonFormatter.formatItemDescription(taxon));
            builder.increaseIndent();

            String currentItemSubheading = null;
            StringBuilder itemSubheadingGroupBuilder = new StringBuilder();

            for (Character ch : _characters) {
                Attribute attr = characterAttributesMap.get(ch).get(taxon.getItemNumber() - 1);
                currentItemSubheading = groupedByItemSubheadingReportHandleAttribute(builder, itemSubheadingGroupBuilder, attr, charactersItemSubheadingMap, currentItemSubheading,
                        displayInapplicables, displayUnknowns, characterFormatter, attributeFormatter);

            }

            if (itemSubheadingGroupBuilder.length() > 0) {
                builder.appendText(itemSubheadingGroupBuilder.toString());
            }

            builder.appendText("");
            builder.decreaseIndent();
        }

        builder.endDocument();

        return builder;
    }

    // Helper method for generateReportGroupedByItemSubheading()
    private String groupedByItemSubheadingReportHandleAttribute(RTFBuilder builder, StringBuilder itemSubheadingGroupBuilder, Attribute attr, Map<Character, String> charactersItemSubheadingMap,
            String currentItemSubheading, boolean displayInapplicables, boolean displayUnknowns, CharacterFormatter characterFormatter, AttributeFormatter attributeFormatter) {

        Character ch = attr.getCharacter();

        if ((!attr.isInapplicable() || displayInapplicables) && (!attr.isUnknown() || displayUnknowns)) {
            
            //If this is the beginning of a new itemsubheading category then we need to add the item subheading
            String itemSubheading = charactersItemSubheadingMap.get(ch);
            if (itemSubheading != null && !itemSubheading.equals(currentItemSubheading)) {
                if (itemSubheadingGroupBuilder.length() > 0) {
                    builder.appendText(itemSubheadingGroupBuilder.toString());
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
            List<Attribute> attrs = dataset.getAttributesForCharacter(ch.getCharacterId());
            characterAttributesMap.put(ch, attrs);
        }

        return characterAttributesMap;
    }

}
