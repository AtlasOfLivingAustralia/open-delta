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

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DiffUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFWriter;
import au.org.ala.delta.util.Pair;

public class SimilaritiesDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<File> {

    private MatchType _matchType;
    private boolean _matchUnknowns = false;
    private boolean _matchInapplicables = false;

    private boolean _useGlobalMatchValues = true;

    private List<Character> _characters;
    private List<Item> _taxa;
    private boolean _includeSpecimen = false;

    private CharacterFormatter _characterFormatter;
    private ItemFormatter _taxonFormatter;
    private AttributeFormatter _attributeFormatter;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        this._includeSpecimen = pair.getSecond();
    }

    public void setMatchOverlap(boolean matchOverlap) {
        if (matchOverlap) {
            _matchType = MatchType.OVERLAP;
            _useGlobalMatchValues = false;
        }
    }

    public void setMatchSubset(boolean matchSubset) {
        if (matchSubset) {
            _matchType = MatchType.SUBSET;
            _useGlobalMatchValues = false;
        }
    }

    public void setMatchExact(boolean matchExact) {
        if (matchExact) {
            _matchType = MatchType.EXACT;
            _useGlobalMatchValues = false;
        }
    }

    public void setMatchUnknowns(boolean matchUnknowns) {
        this._matchUnknowns = matchUnknowns;
        _useGlobalMatchValues = false;
    }

    public void setMatchInapplicables(boolean matchInapplicables) {
        this._matchInapplicables = matchInapplicables;
        _useGlobalMatchValues = false;
    }

    @Override
    public File doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        int numberOfTaxa = _taxa.size();
        if (_includeSpecimen) {
            numberOfTaxa++;
        }

        if (numberOfTaxa < 2) {
            throw new IntkeyDirectiveInvocationException(String.format("At least two taxa required for comparison."));
        }

        if (_useGlobalMatchValues) {
            _matchType = context.getMatchType();
            _matchUnknowns = context.getMatchUnknowns();
            _matchInapplicables = context.getMatchInapplicables();
        }

        _characterFormatter = new CharacterFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, true);
        _taxonFormatter = new ItemFormatter(context.displayNumbering(), CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, false, false, false);
        _attributeFormatter = new AttributeFormatter(context.displayNumbering(), false, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.RETAIN, false, context
                .getDataset().getOrWord());

        Specimen specimen = null;
        if (_includeSpecimen) {
            specimen = context.getSpecimen();
        }

        progress(UIUtils.getResourceString("SimilaritiesDirective.Progress.Calculating"));

        List<au.org.ala.delta.model.Character> similarities = DiffUtils.determineSimilaritiesForTaxa(context.getDataset(), _characters, _taxa, specimen, _matchUnknowns, _matchInapplicables,
                _matchType);

        try {
            // Similarities output can be very large so write it to a temporary
            // file.
            File tempFile = File.createTempFile("IntkeySimilarities", null);
            tempFile.deleteOnExit();
            FileWriter fw = new FileWriter(tempFile);
            RTFWriter rtfWriter = new RTFWriter(fw);
            rtfWriter.startDocument();

            for (int i = 0; i < similarities.size(); i++) {
                int progressPercent = (int) Math.floor((((double) i + 1) / similarities.size()) * 100);
                progress(UIUtils.getResourceString("SimilaritiesDirective.Progress.Generating", progressPercent));

                au.org.ala.delta.model.Character ch = similarities.get(i);

                List<Attribute> attrs = context.getDataset().getAllAttributesForCharacter(ch.getCharacterId());

                String charDescription = _characterFormatter.formatCharacterDescription(ch);
                rtfWriter.writeText(charDescription);

                rtfWriter.increaseIndent();

                if (_includeSpecimen) {
                    rtfWriter.writeText(UIUtils.getResourceString("DifferencesDirective.Specimen"));
                    Attribute attr = specimen.getAttributeForCharacter(ch);

                    rtfWriter.increaseIndent();

                    rtfWriter.writeText(_attributeFormatter.formatAttribute(attr));

                    rtfWriter.decreaseIndent();
                }

                for (Item taxon : _taxa) {
                    Attribute taxonAttr = attrs.get(taxon.getItemNumber() - 1);

                    String taxonDescription = _taxonFormatter.formatItemDescription(taxon);
                    rtfWriter.writeText(taxonDescription);

                    rtfWriter.increaseIndent();

                    rtfWriter.writeText(_attributeFormatter.formatAttribute(taxonAttr));

                    rtfWriter.decreaseIndent();

                }

                rtfWriter.decreaseIndent();
                rtfWriter.writeText("");
            }

            rtfWriter.setTextColor(Color.RED);
            rtfWriter.setFont(1);

            if (similarities.size() == 0) {
                rtfWriter.writeText(UIUtils.getResourceString("SimilaritiesDirective.NoDifferences"));
            } else if (similarities.size() == 1) {
                rtfWriter.writeText(UIUtils.getResourceString("SimilaritiesDirective.OneDifference"));
            } else {
                rtfWriter.writeText(UIUtils.getResourceString("SimilaritiesDirective.ManyDifferences", similarities.size()));
            }

            rtfWriter.endDocument();
            return tempFile;
        } catch (IOException ex) {
            throw new IntkeyDirectiveInvocationException(ex, "Error generating similarities report: %s.", ex.getMessage());
        }
    }

    @Override
    protected void handleProcessingDone(IntkeyContext context, File result) {
        context.getUI().displayRTFReportFromFile(result, UIUtils.getResourceString("SimilaritiesDirective.ReportTitle"));
    }
}
