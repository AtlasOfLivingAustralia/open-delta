package au.org.ala.delta.intkey.directives;

import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetDiagTypeSpecimensDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDiagTypeSpecimensDirective extends NewIntkeyDirective {

    public SetDiagTypeSpecimensDirective() {
        super(false, "set", "diagtype", "specimens");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetDiagTypeSpecimensDirectiveInvocation();
    }

}
