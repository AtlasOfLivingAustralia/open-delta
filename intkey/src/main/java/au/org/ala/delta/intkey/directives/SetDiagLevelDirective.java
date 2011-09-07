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
    protected List<IntkeyDirectiveArgument> buildArguments() {
        List<IntkeyDirectiveArgument> arguments = new ArrayList<IntkeyDirectiveArgument>();
        arguments.add(new IntegerArgument("diaglevel", "Enter value of DiagLevel"));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlags() {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('X', "fakeFlag"));
        return flags;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetDiagLevelInvocation();
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return null;
    }

}
