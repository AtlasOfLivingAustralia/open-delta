package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayContinuousDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayContinuousDirective extends OnOffDirective {
    public DisplayContinuousDirective() {
        super(false, "display", "continuous");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayContinuousDirectiveInvocation();
    }
}
