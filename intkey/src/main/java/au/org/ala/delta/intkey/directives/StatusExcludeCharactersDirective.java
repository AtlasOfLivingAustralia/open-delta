package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusExcludeCharactersDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusIncludeCharactersDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusExcludeCharactersDirective extends IntkeyDirective {
    public StatusExcludeCharactersDirective() {
        super(true, "status", "exclude", "characters");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusExcludeCharactersDirectiveInvocation();
    }
}
