package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetImagePathDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetImagePathDirective extends NewIntkeyDirective {

    public SetImagePathDirective() {
        super("set", "imagepath");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new StringArgument("imagePaths", "Enter the path specification for image files", null));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetImagePathDirectiveInvocation();
    }
}
