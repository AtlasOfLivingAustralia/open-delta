package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusExcludeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusIncludeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusExcludeTaxaDirective extends IntkeyDirective {
    public StatusExcludeTaxaDirective() {
        super(true, "status", "exclude", "taxa");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusExcludeTaxaDirectiveInvocation();
    }
}
