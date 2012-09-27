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
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DefineButtonClearDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.DefineButtonDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.DefineButtonSpaceDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonDirective extends IntkeyDirective {
    
    public static final String SPACE_KEYWORD = "space";
    public static final String CLEAR_KEYWORD = "clear";

    public DefineButtonDirective() {
        super(false, "define", "button");
    }

    @Override
    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        //Need to prompt if no tokens, or data starts with a wildcard
        if (tokens.isEmpty() || data.toUpperCase().startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            List<Object> buttonDefinitionValues = context.getDirectivePopulator().promptForButtonDefinition();

            if (buttonDefinitionValues == null) {
                // cancelled
                return null;
            } else {
                boolean isInsertSpace = (Boolean) buttonDefinitionValues.get(0);
                boolean isRemoveAllButtons = (Boolean) buttonDefinitionValues.get(1);

                if (isInsertSpace) {
                    return new DefineButtonSpaceDirectiveInvocation();
                } else if (isRemoveAllButtons) {
                    return new DefineButtonClearDirectiveInvocation();
                } else {
                    String imageFilePath = (String) buttonDefinitionValues.get(2);

                    String commandsString = (String) buttonDefinitionValues.get(3);

                    List<String> commands = new ArrayList<String>();
                    for (String str : ParsingUtils.removeEnclosingQuotes(commandsString).split(";")) {
                        commands.add(str.trim());
                    }

                    String shortHelp = (String) buttonDefinitionValues.get(4);
                    String fullHelp = (String) buttonDefinitionValues.get(5);
                    boolean enableIfUsedCharactersOnly = (Boolean) buttonDefinitionValues.get(6);
                    boolean enableInNormalModeOnly = (Boolean) buttonDefinitionValues.get(7);
                    boolean enableInAdvancedModeOnly = (Boolean) buttonDefinitionValues.get(8);

                    return new DefineButtonDirectiveInvocation(enableInAdvancedModeOnly, enableInNormalModeOnly, enableIfUsedCharactersOnly, imageFilePath, commands, shortHelp, fullHelp);
                }
            }

        } else {
            String firstToken = tokens.get(0);

            if (firstToken.equalsIgnoreCase(SPACE_KEYWORD)) {
                return processInsertButtonSpace();
            } else if (firstToken.equalsIgnoreCase(CLEAR_KEYWORD)) {
                return processClearButtons();
            } else {
                return processDefineButton(tokens, context);
            }
        }
    }

    private BasicIntkeyDirectiveInvocation processDefineButton(List<String> tokens, IntkeyContext context) throws IntkeyDirectiveParseException {
        boolean displayAdvancedOnly = false;
        boolean displayNormalOnly = false;
        boolean inactiveUnlessUsed = false;

        String fileName = null;
        List<String> directivesToRun = null;
        String shortHelp = null;
        String fullHelp = null;

        for (String token : tokens) {
            if (token.equals("/A")) {
                displayAdvancedOnly = true;
            } else if (token.equals("/N")) {
                displayNormalOnly = true;
            } else if (token.equals("/U")) {
                inactiveUnlessUsed = true;
            } else if (fileName == null) {
                fileName = token;
            } else if (directivesToRun == null) {
                directivesToRun = new ArrayList<String>();
                for (String str : ParsingUtils.removeEnclosingQuotes(token).split(";")) {
                    directivesToRun.add(str.trim());
                }
            } else if (shortHelp == null) {
                shortHelp = ParsingUtils.removeEnclosingQuotes(token);
            } else if (fullHelp == null) {
                fullHelp = ParsingUtils.removeEnclosingQuotes(token);
            }
        }

        if (fileName == null || directivesToRun == null || shortHelp == null) {
            throw new IntkeyDirectiveParseException("InvalidButtonDefinition.error");
        } else {
            return new DefineButtonDirectiveInvocation(displayAdvancedOnly, displayNormalOnly, inactiveUnlessUsed, fileName, directivesToRun, shortHelp, fullHelp);
        }
    }

    private BasicIntkeyDirectiveInvocation processInsertButtonSpace() {
        DefineButtonSpaceDirectiveInvocation invoc = new DefineButtonSpaceDirectiveInvocation();
        invoc.setStringRepresentation(getControlWordsAsString() + " " + SPACE_KEYWORD);
        return invoc;
    }

    private BasicIntkeyDirectiveInvocation processClearButtons() {
        DefineButtonClearDirectiveInvocation invoc = new DefineButtonClearDirectiveInvocation();
        invoc.setStringRepresentation(getControlWordsAsString() + " " + CLEAR_KEYWORD);
        return invoc;
    }

}
