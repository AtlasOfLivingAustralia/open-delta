package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetDemonstrationDirectiveInvocation;

public class SetDemonstrationDirective extends OnOffDirective {

    public SetDemonstrationDirective() {
        super(true, "set", "demonstration");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetDemonstrationDirectiveInvocation();
    }

}
