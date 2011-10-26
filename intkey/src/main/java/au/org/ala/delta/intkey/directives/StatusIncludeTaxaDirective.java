package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusIncludeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusIncludeTaxaDirective extends IntkeyDirective {
    public StatusIncludeTaxaDirective() {
        super(true, "status", "include", "taxa");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusIncludeTaxaDirectiveInvocation();
    }
}
