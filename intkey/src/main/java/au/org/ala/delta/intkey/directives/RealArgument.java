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

public class RealArgument extends IntkeyDirectiveArgument<Double> {

    public RealArgument(String name, String promptText, double initialValue) {
        super(name, promptText, initialValue);
    }

    @Override
    public Double parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();

        if (token == null || token.startsWith(DEFAULT_DIALOG_WILDCARD)) {
            token = context.getDirectivePopulator().promptForString(getPromptText(), Double.toString(_initialValue), directiveName);
        }

        if (token != null) {
            try {
                double parsedDouble = Double.parseDouble(token);
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(parsedDouble);
                return parsedDouble;
            } catch (NumberFormatException ex) {
                throw new IntkeyDirectiveParseException("Real value required");
            }
        }
        return null;
    }

}
