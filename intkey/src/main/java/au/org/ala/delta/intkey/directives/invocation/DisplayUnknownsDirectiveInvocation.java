package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayUnknownsDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        context.setDisplayUnknowns(_value);
        return true;
    }

}
