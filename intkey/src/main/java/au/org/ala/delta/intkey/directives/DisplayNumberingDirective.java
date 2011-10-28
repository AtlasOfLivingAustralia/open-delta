package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayNumberingDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayNumberingDirective extends OnOffDirective {

    public DisplayNumberingDirective() {
        super(false, "display", "numbering");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayNumberingDirectiveInvocation();
    }

}
