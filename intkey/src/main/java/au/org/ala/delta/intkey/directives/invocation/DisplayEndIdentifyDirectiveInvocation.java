package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayEndIdentifyDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        context.setDisplayEndIdentify(_value);
        return true;
    }

}
