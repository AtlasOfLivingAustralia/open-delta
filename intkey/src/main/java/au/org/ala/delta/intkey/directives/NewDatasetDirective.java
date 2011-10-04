package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.NewDatasetDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * The NEWDATASET directive - tells intkey to open the specified dataset -
 * identified by its initialization file (an ini or ink file). All the commands
 * listed in this initalization file will be executed.
 * 
 * @author ChrisF
 * 
 */
public class NewDatasetDirective extends NewIntkeyDirective {

    public NewDatasetDirective() {
        super("newdataset");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new FileArgument("file", "Data Initialization Files (*.ini, *.ink)", null, Arrays.asList(new String[] { "ini", "ink" }), false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new NewDatasetDirectiveInvocation();
    }

}
