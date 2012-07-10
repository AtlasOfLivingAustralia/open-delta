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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Utils;

public class TaxonListArgument extends AbstractTaxonListArgument<List<Item>> {

    /**
     * @param name
     *            Name of the argument
     * @param promptText
     *            Text used to prompt the user for a value
     * @param defaultSelectionMode
     *            default selection mode when user is prompted for a value
     * @param selectFromAll
     *            When prompting, allow selection from all
     */
    public TaxonListArgument(String name, String promptText, boolean selectFromAll, boolean noneSelectionPermitted) {
        super(name, promptText, selectFromAll, noneSelectionPermitted);
    }

    @Override
    public List<Item> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        List<String> selectedKeywordsOrTaxonNumbers = new ArrayList<String>();

        boolean overrideExcludedTaxa = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_TAXA)) {
            overrideExcludedTaxa = true;
            token = inputTokens.poll();
        }

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

                while (token != null) {
                    try {
                        taxa.addAll(ParsingUtils.parseTaxonToken(token, context));
                        selectedKeywordsOrTaxonNumbers.add(token);
                        token = inputTokens.poll();

                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(String.format("Unrecognized taxon keyword %s", token));
                    }
                }

                if (!(overrideExcludedTaxa || _selectFromAll)) {
                    taxa.retainAll(context.getIncludedTaxa());
                }
            }
        }

        if (taxa == null) {
            List<String> selectedKeywords = new ArrayList<String>();
            DirectivePopulator populator = context.getDirectivePopulator();
            if (selectionMode == SelectionMode.KEYWORD) {
                taxa = populator.promptForTaxaByKeyword(directiveName, !(overrideExcludedTaxa || _selectFromAll), _noneSelectionPermitted, false, null, selectedKeywords);
            } else {
                boolean autoSelectSingleValue = (selectionMode == SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE);
                taxa = populator.promptForTaxaByList(directiveName, !(overrideExcludedTaxa || _selectFromAll), autoSelectSingleValue, false, false, null, selectedKeywords);
            }

            if (taxa == null) {
                // cancelled
                return null;
            }

            // Put selected keywords or taxon numbers into a collection to use
            // to build the string representation.
            if (!selectedKeywords.isEmpty()) {
                for (String selectedKeyword : selectedKeywords) {
                    if (selectedKeyword.contains(" ")) {
                        // Enclose any keywords that contain spaces in quotes
                        // for the string representation
                        selectedKeywordsOrTaxonNumbers.add("\"" + selectedKeyword + "\"");
                    } else {
                        selectedKeywordsOrTaxonNumbers.add(selectedKeyword);
                    }
                }
            } else {
                List<Integer> selectedTaxonNumbers = new ArrayList<Integer>();
                for (int i = 0; i < taxa.size(); i++) {
                    Item taxon = taxa.get(i);
                    selectedTaxonNumbers.add(taxon.getItemNumber());
                }
                selectedKeywordsOrTaxonNumbers.add(Utils.formatIntegersAsListOfRanges(selectedTaxonNumbers));
            }
        }

        // build the string representation of the directive call
        stringRepresentationBuilder.append(" ");

        if (overrideExcludedTaxa) {
            stringRepresentationBuilder.append(OVERRIDE_EXCLUDED_TAXA);
            stringRepresentationBuilder.append(" ");
        }

        stringRepresentationBuilder.append(StringUtils.join(selectedKeywordsOrTaxonNumbers, " "));

        if (taxa.size() == 0 && !_noneSelectionPermitted) {
            throw new IntkeyDirectiveParseException("NoTaxaInSet.error");
        }

        Collections.sort(taxa);

        return taxa;
    }

}
