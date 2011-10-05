package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.RestartDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * The RESTART directive - prepares the program for a new identification or
 * query.
 * 
 * @author ChrisF
 * 
 */
public class RestartDirective extends NewIntkeyDirective {

    public RestartDirective() {
        super("restart");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('I', "identificationParameters"));
        flags.add(new IntkeyDirectiveFlag('Q', "queryParameters"));
        flags.add(new IntkeyDirectiveFlag('T', "zeroTolerance"));
        return flags;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new RestartDirectiveInvocation();
    }
}
