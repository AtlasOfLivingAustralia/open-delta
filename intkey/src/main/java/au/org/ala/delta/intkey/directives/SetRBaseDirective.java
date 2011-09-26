package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetRBaseDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * The SET RBASE directive - sets the base of the logarithmic
 * character-reliability scale which is used in determining the 'best'
 * characters during an identification. The default value is 1.1, valid values
 * are real numbers in the range 1 to 5.
 * 
 * @author ChrisF
 * 
 */
public class SetRBaseDirective extends NewIntkeyDirective {
    
    //TODO needs to provide a prompt window if no value is supplied
    
    public SetRBaseDirective() {
        super("set", "rbase");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new RealArgument("rbase", "Enter value of RBASE", context.getRBase()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetRBaseDirectiveInvocation();
    }

}
