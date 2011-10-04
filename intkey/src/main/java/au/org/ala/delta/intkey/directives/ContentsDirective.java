package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.ContentsDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class ContentsDirective extends NewIntkeyDirective {

    public ContentsDirective() {
        super("contents");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new FileArgument("file", "Files (*.ind)", null, Arrays.asList(new String[] { "ind" }), false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new ContentsDirectiveInvocation();
    }

}
