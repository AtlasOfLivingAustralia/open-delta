package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.FileCloseDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayWindowingDirective extends IntkeyDirective {
    public DisplayWindowingDirective() {
        super(false, "display", "windowing");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new FileCloseDirectiveInvocation();
    }

}
