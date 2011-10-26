package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusSetDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusSetDirective extends IntkeyDirective {

    public StatusSetDirective() {
        super(true, "status", "set");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusSetDirectiveInvocation();
    }

}
