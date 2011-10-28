package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayInapplicablesDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayInapplicablesDirective extends OnOffDirective {

    public DisplayInapplicablesDirective() {
        super(false, "display", "inapplicables");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayInapplicablesDirectiveInvocation();
    }

}
