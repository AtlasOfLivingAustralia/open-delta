package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetFixDirectiveInvocation;

public class SetFixDirective extends OnOffDirective {

    public SetFixDirective() {
        super(true, "set", "fix");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetFixDirectiveInvocation();
    }

}
