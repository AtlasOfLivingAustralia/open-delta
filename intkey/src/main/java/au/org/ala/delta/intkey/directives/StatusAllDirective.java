package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusAllDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusAllDirective extends IntkeyDirective {
    public StatusAllDirective() {
        super(true, "status", "all");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusAllDirectiveInvocation();
    }
}
