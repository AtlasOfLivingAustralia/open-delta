package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayEndIdentifyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayEndIdentifyDirective extends OnOffDirective {

    public DisplayEndIdentifyDirective() {
        super(false, "display", "endidentify");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayEndIdentifyDirectiveInvocation();
    }

}
