package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetAutoToleranceDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        context.setAutoTolerance(_value);
        return true;
    }

}
