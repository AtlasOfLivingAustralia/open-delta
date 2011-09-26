package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyCharacterOrder;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayCharacterOrderBestDirectiveInvocation extends IntkeyDirectiveInvocation {
    @Override
    public boolean execute(IntkeyContext context) {
        context.setCharacterOrder(IntkeyCharacterOrder.BEST);
        return true;
    }
}
