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

import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetMatchDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.MatchType;

public class SetMatchDirective extends IntkeyDirective {

    public static final String OVERLAP_LETTER = "o";
    public static final String OVERLAP_WORD = "overlap";
    public static final String SUBSET_LETTER = "s";
    public static final String SUBSET_WORD = "subset";
    public static final String EXACT_LETTER = "e";
    public static final String EXACT_WORD = "exact";
    public static final String UNKNOWNS_LETTER = "u";
    public static final String UNKNOWNS_WORD = "unknowns";
    public static final String INAPPLICABLES_LETTER = "i";
    public static final String INAPPLICABLES_WORD = "inapplicables";

    public SetMatchDirective() {
        super(false, "set", "match");
    }

    @Override
    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        StringBuilder stringRepresentationBuilder = new StringBuilder(getControlWordsAsString());

        boolean matchUnknowns = false;
        boolean matchInapplicables = false;
        MatchType matchType = MatchType.EXACT;

        if (data == null || data.toUpperCase().startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            List<Object> matchSettings = context.getDirectivePopulator().promptForMatchSettings();
            if (matchSettings == null) {
                // Null list indicates that the user cancelled the operation
                return null;
            } else {
                matchUnknowns = (Boolean) matchSettings.get(0);
                matchInapplicables = (Boolean) matchSettings.get(1);
                matchType = (MatchType) matchSettings.get(2);

                switch (matchType) {
                case OVERLAP:
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(OVERLAP_WORD);
                    break;
                case SUBSET:
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(SUBSET_WORD);
                    break;
                case EXACT:
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(EXACT_WORD);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized match type");
                }

                if (matchUnknowns) {
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(UNKNOWNS_WORD);
                }

                if (matchInapplicables) {
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(INAPPLICABLES_WORD);
                }
            }
        } else {
            List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
            for (String token : tokens) {
                if (token.equalsIgnoreCase(OVERLAP_LETTER) || token.equalsIgnoreCase(OVERLAP_WORD)) {
                    matchType = MatchType.OVERLAP;
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(token);
                } else if (token.equalsIgnoreCase(SUBSET_LETTER) || token.equalsIgnoreCase(SUBSET_WORD)) {
                    matchType = MatchType.SUBSET;
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(token);
                } else if (token.equalsIgnoreCase(EXACT_LETTER) || token.equalsIgnoreCase(EXACT_WORD)) {
                    matchType = MatchType.EXACT;
                    matchUnknowns = false;
                    matchInapplicables = false;
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(token);
                } else if (token.equalsIgnoreCase(UNKNOWNS_LETTER) || token.equalsIgnoreCase(UNKNOWNS_WORD)) {
                    matchUnknowns = true;
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(token);
                } else if (token.equalsIgnoreCase(INAPPLICABLES_LETTER) || token.equalsIgnoreCase(INAPPLICABLES_WORD)) {
                    matchInapplicables = true;
                    stringRepresentationBuilder.append(" ");
                    stringRepresentationBuilder.append(token);
                } else {
                    throw new IntkeyDirectiveParseException("InvalidSetMatchOption.error", token);
                }
            }
        }

        SetMatchDirectiveInvocation invoc = new SetMatchDirectiveInvocation(matchInapplicables, matchUnknowns, matchType);
        invoc.setStringRepresentation(stringRepresentationBuilder.toString());
        return invoc;
    }
}
