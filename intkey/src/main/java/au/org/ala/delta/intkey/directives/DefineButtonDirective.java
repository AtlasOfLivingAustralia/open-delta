package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DefineButtonClearDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.DefineButtonDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.DefineButtonSpaceDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonDirective extends IntkeyDirective {

    public DefineButtonDirective() {
        super("define", "button");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);

        if (tokens.isEmpty()) {
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

            if (firstToken.equalsIgnoreCase("space")) {
                return processInsertButtonSpace();
            } else if (firstToken.equalsIgnoreCase("clear")) {
                return processClearButtons();
            } else {
                return processDefineButton(tokens, context);
            }
        }
    }

    private IntkeyDirectiveInvocation processDefineButton(List<String> tokens, IntkeyContext context) {
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
            context.getDirectivePopulator().promptForButtonDefinition();
            return null;
        } else {
            return new DefineButtonDirectiveInvocation(displayAdvancedOnly, displayNormalOnly, inactiveUnlessUsed, fileName, directivesToRun, shortHelp, fullHelp);
        }
    }

    private IntkeyDirectiveInvocation processInsertButtonSpace() {
        return new DefineButtonSpaceDirectiveInvocation();
    }

    private IntkeyDirectiveInvocation processClearButtons() {
        return new DefineButtonClearDirectiveInvocation();
    }

}
