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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class DiagnoseDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;
    private List<Character> _presetCharacters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // the SPECIMEN cannot be selected with the taxa for this directive.
        // Simply ignore
        // it if "SPECIMEN" is supplied
    }

    public void setCharacters(List<Character> characters) {
        this._presetCharacters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        int diagLevel = context.getDiagLevel();
        DiagType diagType = context.getDiagType();

        // Remove any exclude taxa from the supplied list of taxa, and any
        // excluded characters from the
        // supplied list of preset characters
        _taxa.removeAll(context.getExcludedTaxa());
        _presetCharacters.removeAll(context.getExcludedCharacters());

        IntkeyDataset dataset = context.getDataset();

        ItemFormatter itemFormatter = new ItemFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        ItemFormatter noNumberingRTFCommentsItemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, true);
        ItemFormatter noCommentsItemFormatter = new ItemFormatter(context.displayNumbering(), CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, false, true);
        
        CharacterFormatter characterFormatter = new CharacterFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(context.displayNumbering(), false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, context.getDataset()
                .getOrWord());
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        // derive diagnostic character set for specified items from set of
        // masked-in characters.
        for (Item taxon : _taxa) {
            // output taxon name
            builder.appendText(itemFormatter.formatItemDescription(taxon));
            builder.increaseIndent();

            Specimen specimen = new Specimen(context.getDataset(), true, true, true, MatchType.OVERLAP);

            List<Character> remainingCharacters = context.getIncludedCharacters();
            remainingCharacters.removeAll(context.getDataset().getCharactersToIgnoreForBest());
            updateRemainingCharactersFromSpecimen(remainingCharacters, specimen);

            List<Item> remainingTaxa = new ArrayList<Item>(_taxa);
            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);

            // process preset characters first
            for (Character ch : _presetCharacters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());
                useAttribute(specimen, attr, diagType, diagLevel, remainingCharacters, remainingTaxa, characterFormatter, attributeFormatter, builder);
            }

            // calculate further separation characters for current taxon
            boolean diagLevelNotAttained = false;
            if (remainingTaxa.size() > 1) {
                for (int i = 1; i < diagLevel + 1; i++) {
                    List<Item> remainingTaxaForDiagLevel = new ArrayList<Item>(remainingTaxa);
                    updateRemainingTaxaFromSpecimen(remainingTaxaForDiagLevel, specimen, i);

                    while (remainingTaxaForDiagLevel.size() > 1) {
                        LinkedHashMap<Character, Double> bestOrdering = Best.orderDiagnose(taxon.getItemNumber(), diagType, context.getStopBest(), dataset,
                                characterListToIntegerList(remainingCharacters), taxonListToIntegerList(remainingTaxaForDiagLevel), context.getRBase(), context.getVaryWeight());
                        if (bestOrdering.isEmpty()) {
                            break;
                        }

                        Character firstDiagnoseChar = bestOrdering.keySet().iterator().next();

                        Attribute attr = dataset.getAttribute(taxon.getItemNumber(), firstDiagnoseChar.getCharacterId());

                        useAttribute(specimen, attr, diagType, i, remainingCharacters, remainingTaxaForDiagLevel, characterFormatter, attributeFormatter, builder);
                    }

                    builder.setTextColor(Color.RED);
                    if (remainingTaxaForDiagLevel.size() == 1) {
                        builder.appendText(MessageFormat.format("Diagnostic level {0} attained.", i));
                    } else {
                        // If we have failed to reach a diagnostic level, output a message to this effect.
                        // Continue to use and output further diagnostic characters however.
                        if (!diagLevelNotAttained) {
                            diagLevelNotAttained = true;
                            builder.appendText(MessageFormat.format("Diagnostic level {0} not attained.", i));
                        }
                    }

                    builder.setTextColor(Color.BLACK);
                }
            }
            
            if (diagLevelNotAttained) {
                builder.setTextColor(Color.RED);
                builder.appendText(MessageFormat.format("Diagnosis for {0} is incomplete.", noNumberingRTFCommentsItemFormatter.formatItemDescription(taxon)));
                builder.setTextColor(Color.BLACK);
            }

            builder.decreaseIndent();
            
            // Output number of differences for each remaining taxon if the desired diagLevel was not reached.
            if (diagLevelNotAttained) {
                updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);
                builder.setTextColor(Color.RED);
                builder.appendText(MessageFormat.format("{0} taxa remain.", remainingTaxa.size()));
                builder.setTextColor(Color.BLACK);
                Map<Item, Set<Character>> taxonDifferences = specimen.getTaxonDifferences();
                for (Item remainingTaxon: remainingTaxa) {
                    int numDifferences = taxonDifferences.get(remainingTaxon).size();
                    builder.appendText(MessageFormat.format("({0}) {1}", numDifferences, noCommentsItemFormatter.formatItemDescription(remainingTaxon)));
                }
            }
        }

        builder.endDocument();
        context.getUI().displayRTFReport(builder.toString(), "Diagnose");

        return true;
    }

    private void useAttribute(Specimen specimen, Attribute attr, DiagType diagType, int diagLevel, List<Character> remainingCharacters, List<Item> remainingTaxa,
            CharacterFormatter characterFormatter, AttributeFormatter attributeFormatter, RTFBuilder builder) {

        // Ignore "maybe inapplicable" characters if DiagType is SPECIMENS
        if (!attr.isUnknown() && (!attr.isInapplicable() || diagType == DiagType.TAXA)) {
            builder.appendText(String.format("%s %s", characterFormatter.formatCharacterDescription(attr.getCharacter()), attributeFormatter.formatAttribute(attr)));

            specimen.setAttributeForCharacter(attr.getCharacter(), attr);

            updateRemainingCharactersFromSpecimen(remainingCharacters, specimen);
            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);
        }
    }

    private List<Integer> characterListToIntegerList(List<Character> characters) {
        List<Integer> characterNumbers = new ArrayList<Integer>();

        for (Character ch : characters) {
            characterNumbers.add(ch.getCharacterId());
        }

        return characterNumbers;
    }

    private List<Integer> taxonListToIntegerList(List<Item> taxa) {
        List<Integer> taxaNumbers = new ArrayList<Integer>();

        for (Item taxon : taxa) {
            taxaNumbers.add(taxon.getItemNumber());
        }

        return taxaNumbers;
    }

    private void updateRemainingCharactersFromSpecimen(List<Character> remainingCharacters, Specimen specimen) {
        remainingCharacters.removeAll(specimen.getUsedCharacters());
        remainingCharacters.removeAll(specimen.getInapplicableCharacters());
    }

    private void updateRemainingTaxaFromSpecimen(List<Item> remainingTaxa, Specimen specimen, int diagLevel) {
        Map<Item, Set<Character>> taxonDifferences = specimen.getTaxonDifferences();

        for (Item t : taxonDifferences.keySet()) {
            // tolerance = diaglevel - 1
            if (taxonDifferences.get(t).size() > diagLevel - 1) {
                remainingTaxa.remove(t);
            }
        }
    }

}
