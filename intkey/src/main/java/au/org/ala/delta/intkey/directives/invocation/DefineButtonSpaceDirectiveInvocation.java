package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonSpaceDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        context.getUI().addToolbarSpace();
        return true;
    }

}
