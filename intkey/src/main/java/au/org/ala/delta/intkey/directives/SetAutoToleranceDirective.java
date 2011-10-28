package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetAutoToleranceDirectiveInvocation;

public class SetAutoToleranceDirective extends OnOffDirective {

    public SetAutoToleranceDirective() {
        super(false, "set", "autotolerance");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetAutoToleranceDirectiveInvocation();
    }
}
