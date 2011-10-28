package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DefineEndIdentifyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineEndIdentifyDirective extends NewIntkeyDirective {

    public DefineEndIdentifyDirective() {
        super(false, "define", "endidentify");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new StringArgument("commands", "Enter the command(s) to be executed", null, true));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('T', "numWindowsToTile", true));
        return flags;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DefineEndIdentifyDirectiveInvocation();
    }

}
