package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DisplayLogDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayLogDirective extends NewIntkeyDirective {

    public DisplayLogDirective() {
        super("display", "log");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new OnOffArgument("value", null, context.getUI().isLogVisible()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayLogDirectiveInvocation();
    }
}
