package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.FileDisplayDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileDisplayDirective extends NewIntkeyDirective {
    
    public FileDisplayDirective() {
        super("file", "display");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new FileArgument("file", "Files (*.rtf, *.doc, *.htm, *.wav, *.ink)", null, Arrays.asList(new String[] { "rtf", "doc", "htm", "html", "wav", "ink" }), false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new FileDisplayDirectiveInvocation();
    }

}
