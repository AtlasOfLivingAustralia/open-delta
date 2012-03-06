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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.impl.AvalonLogger;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class DiagnoseDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;
    private List<Character> _characters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // the SPECIMEN cannot be selected with the taxa for this directive.
        // Simply ignore
        // it if "SPECIMEN" is supplied
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {

        if (_characters.isEmpty()) {
            _characters = context.getAvailableCharacters();
            _characters.removeAll(context.getDataset().getCharactersToIgnoreForBest());
        }

        List<Integer> characterNumbers = new ArrayList<Integer>();
        List<Integer> taxonNumbers = new ArrayList<Integer>();

        for (Character ch : _characters) {
            characterNumbers.add(ch.getCharacterId());
        }

        for (Item taxon : _taxa) {
            taxonNumbers.add(taxon.getItemNumber());
        }

        IntkeyDataset dataset = context.getDataset();

        ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(false, false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, context.getDataset().getOrWord());

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        // saved information which will be altered by DIAGNOSE.

        // derive diagnostic character set for specified items from set of
        // masked-in characters.
        for (Item taxon : _taxa) {

            List<Integer> remainingCharacterNumbers = new ArrayList<Integer>(characterNumbers);
            List<Integer> remainingTaxonNumbers = new ArrayList<Integer>(taxonNumbers);

            // output taxon name
            builder.appendText(itemFormatter.formatItemDescription(taxon));

            Specimen specimen = new Specimen(context.getDataset(), true, true, true, MatchType.OVERLAP);

            // TODO process preset characters first

            // calculate further separation characters for current taxon
            // get diagnose order
            //

            while (remainingTaxonNumbers.size() > 1) {
                LinkedHashMap<Character, Double> bestOrdering = Best.orderDiagnose(taxon.getItemNumber(), dataset, remainingCharacterNumbers, remainingTaxonNumbers, context.getRBase(),
                        context.getVaryWeight());
                Character firstDiagnoseChar = bestOrdering.keySet().iterator().next();

                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), firstDiagnoseChar.getCharacterId());
                
                builder.appendText(attr.toString());

                specimen.setAttributeForCharacter(firstDiagnoseChar, attr);

                Map<Item, Set<Character>> taxonDifferences = specimen.getTaxonDifferences();

                for (Item t : taxonDifferences.keySet()) {
                    if (taxonDifferences.get(t).size() > 0) {
                        remainingTaxonNumbers.remove((Integer)t.getItemNumber());
                    }
                }

                for (Character ch : specimen.getUsedCharacters()) {
                    remainingCharacterNumbers.remove((Integer)ch.getCharacterId());
                }
                
                for (Character ch : specimen.getInapplicableCharacters()) {
                    remainingCharacterNumbers.remove((Integer)ch.getCharacterId());
                }
            }
        }

        builder.endDocument();
        context.getUI().displayRTFReport(builder.toString(), "Diagnose");

        return true;
    }

}
