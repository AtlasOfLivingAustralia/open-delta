package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDemonstrationDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        context.setDemonstrationMode(_value);
        return true;
    }

}
