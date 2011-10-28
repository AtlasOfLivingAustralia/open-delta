package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayScaledDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayScaledDirective extends OnOffDirective {

    public DisplayScaledDirective() {
        super(false, "display", "scaled");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayScaledDirectiveInvocation();
    }

}
