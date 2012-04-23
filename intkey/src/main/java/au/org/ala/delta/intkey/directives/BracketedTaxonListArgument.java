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
package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.mutable.MutableBoolean;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Pair;

/**
 * List of taxa. Must be surrounded in brackets if specifying more than one
 * character number/range/keyword. The "SPECIMEN" keyword can be supplied as one
 * of the items in the list, to indicate that the specimen should also be used
 * while processing the directive.
 * 
 * @author ChrisF
 * 
 */
public class BracketedTaxonListArgument extends AbstractTaxonListArgument<Pair<List<Item>, Boolean>> {

    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";

    public BracketedTaxonListArgument(String name, String promptText, boolean selectFromAll, boolean noneSelectionPermitted) {
        super(name, promptText, selectFromAll, noneSelectionPermitted);
    }

    @Override
    public Pair<List<Item>, Boolean> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        boolean overrideExcludedTaxa = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_TAXA)) {
            overrideExcludedTaxa = true;
            token = inputTokens.poll();
        }

        overrideExcludedTaxa = overrideExcludedTaxa || _selectFromAll;

        boolean includeSpecimen = false;
        List<Item> taxa = null;

        SelectionMode selectionMode = context.displayKeywords() ? SelectionMode.KEYWORD : SelectionMode.LIST;

        if (token != null) {
            if (token.equalsIgnoreCase(DEFAULT_DIALOG_WILDCARD)) {
                // do nothing - default selection mode is already set above.
            } else if (token.equalsIgnoreCase(KEYWORD_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.KEYWORD;
            } else if (token.equalsIgnoreCase(LIST_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.LIST;
            } else if (token.equalsIgnoreCase(LIST_DIALOG_AUTO_SELECT_SOLE_ITEM_WILDCARD)) {
                selectionMode = SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE;
            } else {
                taxa = new ArrayList<Item>();

                boolean inBrackets = false;

                if (token.equals(OPEN_BRACKET)) {
                    inBrackets = true;
                    token = inputTokens.poll();
                }

                while (token != null) {

                    if (token.equals(CLOSE_BRACKET)) {
                        break;
                    }

                    try {

                        if (token.equalsIgnoreCase(IntkeyContext.SPECIMEN_KEYWORD)) {
                            includeSpecimen = true;
                        } else {
                            taxa.addAll(ParsingUtils.parseTaxonToken(token, context));
                        }

                        // If we are expecting a bracketed list, but no brackets
                        // are present,
                        // only parse the first token
                        if (!inBrackets) {
                            break;
                        }

                        token = inputTokens.poll();

                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(String.format("Unrecognized taxon keyword %s", token), ex);
                    }
                }

                if (!overrideExcludedTaxa) {
                    taxa.retainAll(context.getIncludedTaxa());
                }
            }
        }

        if (taxa == null) {
            // The specimen is included as an option for these prompts
            MutableBoolean specimenSelected = new MutableBoolean(false);
            boolean includeSpecimenAsOption = !context.getSpecimen().getUsedCharacters().isEmpty();
            DirectivePopulator populator = context.getDirectivePopulator();
            if (selectionMode == SelectionMode.KEYWORD && context.displayKeywords()) {
                taxa = populator.promptForTaxaByKeyword(directiveName, !overrideExcludedTaxa, _noneSelectionPermitted, includeSpecimenAsOption, specimenSelected);
                
            } else {
                boolean autoSelectSingleValue = (selectionMode == SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE);
                taxa = populator.promptForTaxaByList(directiveName, !overrideExcludedTaxa, autoSelectSingleValue, false, includeSpecimenAsOption, specimenSelected);
            }
            includeSpecimen = specimenSelected.booleanValue();
        }

        if (taxa.size() == 0 && includeSpecimen == false && !_noneSelectionPermitted) {
            throw new IntkeyDirectiveParseException("NoTaxaInSet.error");
        }

        // TODO need to handle keywords here
        stringRepresentationBuilder.append(" ");
        stringRepresentationBuilder.append(OPEN_BRACKET);
        for (int i = 0; i < taxa.size(); i++) {
            Item taxon = taxa.get(i);
            stringRepresentationBuilder.append(taxon.getItemNumber());
            if (i < taxa.size() - 1) {
                stringRepresentationBuilder.append(" ");
            }
        }

        if (includeSpecimen) {
            stringRepresentationBuilder.append(" ");
            stringRepresentationBuilder.append(IntkeyContext.SPECIMEN_KEYWORD.toUpperCase());
        }
        stringRepresentationBuilder.append(CLOSE_BRACKET);

        Collections.sort(taxa);
        return new Pair<List<Item>, Boolean>(taxa, includeSpecimen);
    }
}
