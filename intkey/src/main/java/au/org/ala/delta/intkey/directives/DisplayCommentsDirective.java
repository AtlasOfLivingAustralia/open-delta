package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayCommentsDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class DisplayCommentsDirective extends OnOffDirective {

    public DisplayCommentsDirective() {
        super(false, "display", "comments");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayCommentsDirectiveInvocation();
    }

}
