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

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class OnOffArgument extends IntkeyDirectiveArgument<Boolean> {

    public static final String ON_VALUE = "ON";
    public static final String OFF_VALUE = "OFF";

    public OnOffArgument(String name, String promptText, boolean initialValue) {
        super(name, promptText, initialValue);
    }

    @Override
    public Boolean parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();

        if (token == null || token.startsWith(DEFAULT_DIALOG_WILDCARD)) {
            if (context.isProcessingDirectivesFile()) {
                //ignore incomplete directives when processing an input file
                return null;
            }
            
            return context.getDirectivePopulator().promptForOnOffValue(directiveName, getInitialValue());
        } else {
            if (token.equalsIgnoreCase(ON_VALUE)) {
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(ON_VALUE);
                return true;
            } else if (token.equalsIgnoreCase(OFF_VALUE)) {
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(OFF_VALUE);
                return false;
            } else {
                throw new IntkeyDirectiveParseException(String.format("Invalid value '%s', expecting '%s' or '%s'", token, ON_VALUE, OFF_VALUE));
            }
        }
    }

}
