package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.StopBestDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetStopBestDirective extends NewIntkeyDirective {

    public SetStopBestDirective() {
        super("set", "stopbest");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new IntegerArgument("value", "Enter value of STOPBEST", context.getStopBest()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new StopBestDirectiveInvocation();
    }

}
