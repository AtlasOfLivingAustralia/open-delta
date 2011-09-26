package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayCharacterOrderBestDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayCharacterOrderBestDirective extends IntkeyDirective {

    public DisplayCharacterOrderBestDirective() {
        super("display", "characterorder", "best");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new DisplayCharacterOrderBestDirectiveInvocation();
    }

}
