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

//TODO need to prompt user for reliability value for each subcommand if the 
//value is not supplied by the user.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetReliabilitiesDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Character;

public class SetReliabilitiesDirective extends IntkeyDirective {

    public SetReliabilitiesDirective() {
        super(true, "set", "reliabilities");
    }

    @Override
    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        StringBuilder stringRepresentationBuilder = new StringBuilder();
        stringRepresentationBuilder.append(getControlWordsAsString());
        stringRepresentationBuilder.append(" ");

        IntkeyDataset dataset = context.getDataset();

        Map<Character, Float> reliabilitiesMap = new HashMap<Character, Float>();

        if (data == null || data.toUpperCase().startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD) || data.toUpperCase().startsWith(IntkeyDirectiveArgument.KEYWORD_DIALOG_WILDCARD)
                || data.toUpperCase().startsWith(IntkeyDirectiveArgument.LIST_DIALOG_WILDCARD)) {
            SelectionMode selectionMode = context.displayKeywords() ? SelectionMode.KEYWORD : SelectionMode.LIST;

            if (data != null && data.startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
                // do nothing - default selection mode is already set above.
            } else if (data != null && data.startsWith(IntkeyDirectiveArgument.KEYWORD_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.KEYWORD;
            } else if (data != null && data.startsWith(IntkeyDirectiveArgument.LIST_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.LIST;
            }

            List<Character> characters;
            List<String> selectedKeywords = new ArrayList<String>(); // not
                                                                     // used,
                                                                     // but
                                                                     // needs to
                                                                     // be
                                                                     // supplied
                                                                     // as an
                                                                     // argument
            if (selectionMode == SelectionMode.KEYWORD) {
                characters = context.getDirectivePopulator().promptForCharactersByKeyword(getControlWordsAsString(), true, false, selectedKeywords);
            } else {
                characters = context.getDirectivePopulator().promptForCharactersByList(getControlWordsAsString(), false, selectedKeywords);
            }

            if (characters == null) {
                // cancelled
                return null;
            }

            Float reliability = Float.parseFloat(context.getDirectivePopulator().promptForString("Enter reliability value", null, getControlWordsAsString()));
            for (Character ch : characters) {
                reliabilitiesMap.put(ch, reliability);
            }

            if (!selectedKeywords.isEmpty()) {
                for (int i = 0; i < selectedKeywords.size(); i++) {
                    if (i != 0) {
                        stringRepresentationBuilder.append(" ");
                    }
                    String keyword = selectedKeywords.get(i);
                    if (keyword.contains(" ")) {
                        stringRepresentationBuilder.append("\"" + selectedKeywords.get(i) + "\"");
                    } else {
                        stringRepresentationBuilder.append(selectedKeywords.get(i));
                    }
                    stringRepresentationBuilder.append(",");
                    stringRepresentationBuilder.append(reliability);
                }
            } else {

            }
        } else {
            List<String> subCmds = ParsingUtils.tokenizeDirectiveCall(data);

            for (String subCmd : subCmds) {
                String[] tokens = subCmd.split(",");

                String strCharacters = tokens[0];
                String strReliability = tokens[1];

                List<Character> characters = new ArrayList<Character>();

                IntRange charRange = ParsingUtils.parseIntRange(strCharacters);
                if (charRange != null) {
                    for (int index : charRange.toArray()) {
                        characters.add(dataset.getCharacter(index));
                    }
                } else {
                    characters = context.getCharactersForKeyword(strCharacters);
                }

                float reliability = Float.parseFloat(strReliability);

                for (Character ch : characters) {
                    reliabilitiesMap.put(ch, reliability);
                }
            }
            stringRepresentationBuilder.append(data);
        }

        SetReliabilitiesDirectiveInvocation invoc = new SetReliabilitiesDirectiveInvocation(reliabilitiesMap);
        invoc.setStringRepresentation(stringRepresentationBuilder.toString());
        return invoc;
    }
}
