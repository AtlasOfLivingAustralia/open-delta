package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetVaryWtDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetVaryWtDirective extends NewIntkeyDirective {
    public SetVaryWtDirective() {
        super(false, "set", "varywt");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new RealArgument("varyWt", "Enter value of VARYWT", context.getVaryWeight()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetVaryWtDirectiveInvocation();
    }
}
