package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetDiagLevelInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDiagLevelDirective extends NewIntkeyDirective {

    public SetDiagLevelDirective() {
        super("set", "diaglevel");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new IntegerArgument("diagLevel", "Enter value of DiagLevel", context.getDiagLevel()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetDiagLevelInvocation();
    }

}
