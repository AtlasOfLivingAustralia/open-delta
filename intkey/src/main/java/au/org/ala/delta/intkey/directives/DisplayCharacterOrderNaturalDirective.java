package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayCharacterOrderNaturalDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayCharacterOrderNaturalDirective extends IntkeyDirective{

    public DisplayCharacterOrderNaturalDirective() {
        super(true, "display", "characterorder", "natural");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new DisplayCharacterOrderNaturalDirectiveInvocation();
    }

}
