package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayLogDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayLogDirective extends OnOffDirective {

    public DisplayLogDirective() {
        super(false, "display", "log");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayLogDirectiveInvocation();
    }
}
