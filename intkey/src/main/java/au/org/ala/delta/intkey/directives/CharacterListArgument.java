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
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Utils;

public class CharacterListArgument extends IntkeyDirectiveArgument<List<au.org.ala.delta.model.Character>> {

    private static final String OVERRIDE_EXCLUDED_CHARACTERS = "/C";

    /**
     * If true, excluded characters are ignored when prompting the user to
     * select characters. User will select from the list of all characters.
     */
    private boolean _selectFromAll;

    /**
     * If true, the "NONE" keyword is a permitted option
     */
    protected boolean _noneSelectionPermitted;

    public CharacterListArgument(String name, String promptText, boolean selectFromAll, boolean noneSelectionPermitted) {
        super(name, promptText, null);
        _selectFromAll = selectFromAll;
        _noneSelectionPermitted = noneSelectionPermitted;
    }

    @Override
    public List<au.org.ala.delta.model.Character> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder)
            throws IntkeyDirectiveParseException {

        List<String> selectedKeywordsOrCharacterNumbers = new ArrayList<String>();

        boolean overrideExcludedCharacters = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_CHARACTERS)) {
            overrideExcludedCharacters = true;
            token = inputTokens.poll();
        }

        List<au.org.ala.delta.model.Character> characters = null;

        SelectionMode selectionMode = context.displayKeywords() ? SelectionMode.KEYWORD : SelectionMode.LIST;
        DirectivePopulator populator = context.getDirectivePopulator();

        if (token != null) {
            if (token.equalsIgnoreCase(DEFAULT_DIALOG_WILDCARD)) {
                // do nothing - default selection mode is already set above.
            } else if (token.equalsIgnoreCase(KEYWORD_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.KEYWORD;
            } else if (token.equalsIgnoreCase(LIST_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.LIST;
            } else {
                characters = new ArrayList<au.org.ala.delta.model.Character>();
                while (token != null) {
                    try {
                    characters.addAll(ParsingUtils.parseCharacterToken(token, context));
                    selectedKeywordsOrCharacterNumbers.add(token);
                    token = inputTokens.poll();
                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException("UnrecognizedCharacterKeyword.error", token);
                    }
                }

                if (!(overrideExcludedCharacters || _selectFromAll)) {
                    characters.retainAll(context.getIncludedCharacters());
                }
            }
        }

        if (characters == null) {
            if (context.isProcessingDirectivesFile()) {
                //ignore incomplete directives when processing an input file
                return null;
            }
            
            List<String> selectedKeywords = new ArrayList<String>();
            if (selectionMode == SelectionMode.KEYWORD) {
                characters = populator.promptForCharactersByKeyword(directiveName, !(overrideExcludedCharacters || _selectFromAll), _noneSelectionPermitted, selectedKeywords);
            } else {
                characters = populator.promptForCharactersByList(directiveName, !(overrideExcludedCharacters || _selectFromAll), selectedKeywords);
            }

            if (characters == null) {
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
                        selectedKeywordsOrCharacterNumbers.add("\"" + selectedKeyword + "\"");
                    } else {
                        selectedKeywordsOrCharacterNumbers.add(selectedKeyword);
                    }
                }
            } else {
                List<Integer> selectedCharacterNumbers = new ArrayList<Integer>();
                for (int i = 0; i < characters.size(); i++) {
                    Character ch = characters.get(i);
                    selectedCharacterNumbers.add(ch.getCharacterId());
                }
                selectedKeywordsOrCharacterNumbers.add(Utils.formatIntegersAsListOfRanges(selectedCharacterNumbers));
            }
        }

        // build the string representation of the directive call
        stringRepresentationBuilder.append(" ");

        if (overrideExcludedCharacters) {
            stringRepresentationBuilder.append(OVERRIDE_EXCLUDED_CHARACTERS);
            stringRepresentationBuilder.append(" ");
        }

        stringRepresentationBuilder.append(StringUtils.join(selectedKeywordsOrCharacterNumbers, " "));

        if (characters.size() == 0 && !_noneSelectionPermitted) {
            throw new IntkeyDirectiveParseException("NoCharactersInSet.error");
        }

        Collections.sort(characters);

        return characters;
    }
}
