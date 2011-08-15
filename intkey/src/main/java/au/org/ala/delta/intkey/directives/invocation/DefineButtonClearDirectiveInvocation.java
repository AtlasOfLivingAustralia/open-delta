package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonClearDirectiveInvocation implements IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        System.out.println("Clearing buttons");
        return true;
    }

}
