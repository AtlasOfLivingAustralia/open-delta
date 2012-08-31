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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.model.FormattingUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class OutputDescribeDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<Void> {

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
    public Void doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        IntkeyDataset dataset = context.getDataset();
        ItemFormatter taxonFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false, true);

        context.appendTextToOutputFile("Output Describe");
        context.appendBlankLineToOutputFile();
        
        int numTaxaProcessed = 0;
        updateProgess(numTaxaProcessed, _taxa.size());

        if (_includeSpecimen) {
            StringBuilder builder = new StringBuilder();
            Specimen specimen = context.getSpecimen();

            builder.append("# ");
            builder.append("Specimen");
            builder.append("/");
            builder.append("\n");

            for (int i = 0; i < _characters.size(); i++) {
                Character ch = _characters.get(i);
                if (specimen.hasValueFor(ch)) {
                    Attribute attr = specimen.getAttributeForCharacter(ch);
                    if (!attr.isUnknown()) {
                        builder.append(ch.getCharacterId());
                        builder.append(",");
                        builder.append(getAttributeAsString(attr));
                        if (i < _characters.size() - 1) {
                            builder.append(" ");
                        }
                    }
                }
            }

            context.appendTextToOutputFile(builder.toString());
            context.appendBlankLineToOutputFile();
            
            updateProgess(++numTaxaProcessed, _taxa.size());
        }

        for (Item taxon : _taxa) {
            StringBuilder builder = new StringBuilder();

            builder.append("# ");
            builder.append(taxonFormatter.formatItemDescription(taxon));
            builder.append("/");
            builder.append("\n");

            for (int i = 0; i < _characters.size(); i++) {
                Character ch = _characters.get(i);
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());

                // Include in the output those characters that are not
                // explicitly coded, but are made
                // inapplicable by their controlling characters
                if (!attr.isUnknown() || attr.isInapplicable()) {
                    builder.append(ch.getCharacterId());
                    builder.append(",");
                    builder.append(getAttributeAsString(attr));
                    if (i < _characters.size() - 1) {
                        builder.append(" ");
                    }
                }
            }

            context.appendTextToOutputFile(builder.toString());
            context.appendBlankLineToOutputFile();
            
            updateProgess(++numTaxaProcessed, _taxa.size());
        }

        return null;
    }

    @Override
    protected void handleProcessingDone(IntkeyContext context, Void result) {
        // do nothing - output file is updated by doRunInBackground

    }
    
    private void updateProgess(int numTaxaProcessed, int totalNumTaxa) {
        int progressPercent = (int) Math.floor((((double) numTaxaProcessed) / totalNumTaxa) * 100);
        progress(UIUtils.getResourceString("OutputDescribeDirective.Progress.Generating", progressPercent));
    }

    private String getAttributeAsString(Attribute attr) {
        if (attr instanceof TextAttribute) {
            return getTextAttributeAsString((TextAttribute) attr);
        } else if (attr instanceof RealAttribute) {
            return getRealAttributeAsString((RealAttribute) attr);
        } else if (attr instanceof MultiStateAttribute) {
            return getMultiStateAttributeAsString((MultiStateAttribute) attr);
        } else if (attr instanceof IntegerAttribute) {
            return getIntegerAttributeAsString((IntegerAttribute) attr);
        } else {
            throw new RuntimeException("Unrecognized attribute type");
        }
    }

    private String getTextAttributeAsString(TextAttribute attr) {
        AttributeFormatter attrFormatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN);
        return attrFormatter.formatAttribute(attr);
    }

    private String getMultiStateAttributeAsString(MultiStateAttribute attr) {

        List<String> presentValuesAsString = new ArrayList<String>();
        List<Integer> presentStates = attr.getPresentStatesAsList();

        for (int state : presentStates) {
            presentValuesAsString.add(Integer.toString(state));
        }

        if (attr.isInapplicable()) {
            presentValuesAsString.add("-");
        }

        return StringUtils.join(presentValuesAsString, "/");
    }

    private String getRealAttributeAsString(RealAttribute attr) {
        StringBuilder builder = new StringBuilder();

        FloatRange presentRange = attr.getPresentRange();

        if (presentRange != null) {
            builder.append(String.format("%.2f", presentRange.getMinimumFloat()));

            if (presentRange.getMinimumFloat() != presentRange.getMaximumFloat()) {
                builder.append(String.format("-%.2f", presentRange.getMaximumFloat()));
            }

            if (attr.isInapplicable()) {
                builder.append("/-");
            }

        } else {
            if (attr.isInapplicable()) {
                builder.append("-");
            }
        }
        return builder.toString();
    }

    private String getIntegerAttributeAsString(IntegerAttribute attr) {
        StringBuilder builder = new StringBuilder();
        Set<Integer> presentValues = attr.getPresentValues();
        if (!presentValues.isEmpty()) {
            builder.append(FormattingUtils.formatIntegerValuesAsString(presentValues, attr.getCharacter().getMinimumValue(), attr.getCharacter().getMaximumValue()));
            if (attr.isInapplicable()) {
                builder.append("/-");
            }
        } else {
            if (attr.isInapplicable()) {
                builder.append("-");
            }
        }
        return builder.toString();
    }

}
