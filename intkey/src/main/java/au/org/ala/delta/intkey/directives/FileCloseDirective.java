package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.FileCloseDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileCloseDirective extends IntkeyDirective {

    public FileCloseDirective() {
        super("file", "close");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new FileCloseDirectiveInvocation();
    }

}
