package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayCharacterOrderNaturalDirectiveInvocation extends IntkeyDirectiveInvocation {
    @Override
    public boolean execute(IntkeyContext context) {
        context.setCharacterOrderNatural();
        return true;
    }
}
