package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayUnknownsDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayUnknownsDirective extends OnOffDirective {

    public DisplayUnknownsDirective() {
        super(false, "display", "unknowns");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayUnknownsDirectiveInvocation();
    }

}
