package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetToleranceDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetToleranceDirective extends NewIntkeyDirective {

    public SetToleranceDirective() {
        super(false, "set", "tolerance");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new IntegerArgument("toleranceValue", "Enter value of TOLERANCE", context.getTolerance()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetToleranceDirectiveInvocation();
    }

}
