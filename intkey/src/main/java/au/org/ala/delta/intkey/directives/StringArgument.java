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

public class StringArgument extends IntkeyDirectiveArgument<String> {

    /**
     * ctor
     * 
     * @param name
     *            Argument name
     * @param promptText
     *            text to prompt user if a value is needed for the argument
     * @param initialValue
     *            initialValue to be shown when user is prompted.
     * @param spaceDelimited
     *            if true, any space character that is not surrounded by quotes
     *            will indicate the end of the argument value. If false, all
     *            available data is used to form the argument value
     */

    private boolean _spaceDelimited;

    public StringArgument(String name, String promptText, String initialValue, boolean spaceDelimited) {
        super(name, promptText, initialValue);
        _spaceDelimited = spaceDelimited;
    }

    @Override
    public String parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String argumentValue = null;
        String token = inputTokens.peek();
        if (token == null || token.startsWith(DEFAULT_DIALOG_WILDCARD)) {
            if (context.isProcessingDirectivesFile()) {
                //ignore incomplete directives when processing an input file
                return null;
            }
            
            argumentValue = context.getDirectivePopulator().promptForString(_promptText, _initialValue, directiveName);
        } else if (_spaceDelimited) {
            inputTokens.poll();
            argumentValue = token;
        } else {
            // If argument is not space delimited, we need to use all available
            // tokens in the queue to construct the value for
            // the argument.
            StringBuilder valueBuilder = new StringBuilder();
            while (!inputTokens.isEmpty()) {
                valueBuilder.append(inputTokens.poll());

                if (!inputTokens.isEmpty()) {
                    valueBuilder.append(" ");
                }
            }

            argumentValue = valueBuilder.toString();
        }

        stringRepresentationBuilder.append(" ");
        stringRepresentationBuilder.append(argumentValue);

        return argumentValue;
    }
}
