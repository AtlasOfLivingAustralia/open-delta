package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.QuitDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class QuitDirective extends IntkeyDirective {

    public QuitDirective() {
        super(false, "quit");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new QuitDirectiveInvocation();
    }

}
