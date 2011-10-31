package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayInputDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayInputDirective extends OnOffDirective {

    public DisplayInputDirective() {
        super(false, "display", "input");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayInputDirectiveInvocation();
    }
}
