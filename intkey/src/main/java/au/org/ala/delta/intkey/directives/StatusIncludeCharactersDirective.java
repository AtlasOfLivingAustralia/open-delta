package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusIncludeCharactersDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusIncludeCharactersDirective extends IntkeyDirective {

    public StatusIncludeCharactersDirective() {
        super(true, "status", "include", "characters");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusIncludeCharactersDirectiveInvocation();
    }

}
