package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayScaledDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        context.setDisplayScaled(_value);
        return true;
    }

}
