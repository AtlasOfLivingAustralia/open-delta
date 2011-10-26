package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StatusFilesDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class StatusFilesDirective extends IntkeyDirective {

    public StatusFilesDirective() {
        super(false, "status", "files");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new StatusFilesDirectiveInvocation();
    }

}
