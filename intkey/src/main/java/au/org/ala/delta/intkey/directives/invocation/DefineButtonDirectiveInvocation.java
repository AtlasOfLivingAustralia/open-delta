package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonDirectiveInvocation implements IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        System.out.println("defining button");
        //context.getUI().addToolbarButton(false, false, false, "test.png", "USE 1,1", "short help for button", "full help for button");
        return true;
    }

}
