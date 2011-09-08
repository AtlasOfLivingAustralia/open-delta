package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetFixDirectiveInvocation;

public class SetFixDirective extends NewIntkeyDirective {
    
    public SetFixDirective() {
        super("set", "fix");
    }

    @Override
    protected List<IntkeyDirectiveArgument> buildArguments() {
        List<IntkeyDirectiveArgument> arguments = new ArrayList<IntkeyDirectiveArgument>();
        arguments.add(new OnOffArgument("value", null));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlags() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetFixDirectiveInvocation();
    }

}
