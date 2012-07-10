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

public class IntegerArgument extends IntkeyDirectiveArgument<Integer> {

    public IntegerArgument(String name, String promptText, int initialValue) {
        super(name, promptText, initialValue);
    }

    @Override
    public Integer parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();

        if (token == null || token.startsWith(DEFAULT_DIALOG_WILDCARD)) {
            token = context.getDirectivePopulator().promptForString(_promptText, Integer.toString(_initialValue), directiveName);
        }

        if (token != null) {
            try {
                int parsedInteger = Integer.parseInt(token);
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(parsedInteger);
                return parsedInteger;
            } catch (NumberFormatException ex) {
                throw new IntkeyDirectiveParseException("Integer value required");
            }
        }
        return null;
    }
}
