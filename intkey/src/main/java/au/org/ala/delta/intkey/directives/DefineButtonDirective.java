package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.directives.args.DirectiveArgType;
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
    public int getArgType() {
        // TODO Auto-generated method stub
        return DirectiveArgType.DIRARG_FILE;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);

        if (tokens.isEmpty()) {
            // TODO show button definition dialog
            return null;
        } else {
            String firstToken = tokens.get(0);

            if (firstToken.equalsIgnoreCase("space")) {
                return processInsertButtonSpace();
            } else if (firstToken.equalsIgnoreCase("clear")) {
                return processClearButtons();
            } else {
                return processDefineButton(tokens);
            }
        }
    }

    private IntkeyDirectiveInvocation processDefineButton(List<String> tokens) {
        boolean displayAdvancedOnly = false;
        boolean displayNormalOnly = false;
        boolean inactiveUnlessUsed = false;
        
        String fileName = null;
        List<String> directivesToRun = null;
        String shortHelp = null;
        String fullHelp = null;
        
        for (String token: tokens) {
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
                for (String str: ParsingUtils.removeEnclosingQuotes(token).split(";")) {
                    directivesToRun.add(str.trim());
                }
            } else if (shortHelp == null) {
                shortHelp = token;
            } else if (fullHelp == null) {
                fullHelp = token;
            }
        }

        if (fileName == null || directivesToRun == null || shortHelp == null) {
            // TODO prompt using button definition dialog
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
