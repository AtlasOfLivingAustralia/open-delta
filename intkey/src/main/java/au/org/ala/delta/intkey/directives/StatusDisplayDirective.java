package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusDisplayDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusDisplayDirective extends IntkeyDirective {
    public StatusDisplayDirective() {
        super(true, "status", "display");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusDisplayDirectiveInvocation();
    }
}
