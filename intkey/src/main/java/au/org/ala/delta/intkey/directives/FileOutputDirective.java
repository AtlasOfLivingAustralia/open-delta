package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.FileOutputDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class FileOutputDirective extends NewIntkeyDirective {
    
    public FileOutputDirective() {
        super("file", "output");
    }

    @Override
    protected List<IntkeyDirectiveArgument> buildArguments() {
        List<IntkeyDirectiveArgument> arguments = new ArrayList<IntkeyDirectiveArgument>();
        arguments.add(new FileArgument("file", "Files (*.out)", Arrays.asList(new String[] { "out" }), true));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlags() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new FileOutputDirectiveInvocation();
    }

}
