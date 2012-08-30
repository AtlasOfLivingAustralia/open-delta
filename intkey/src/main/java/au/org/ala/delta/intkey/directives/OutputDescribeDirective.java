package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.OutputDescribeDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class OutputDescribeDirective extends OutputDirective {

    public OutputDescribeDirective() {
        super(true, "output", "describe");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new BracketedTaxonListArgument("selectedTaxaSpecimen", null,  false, false));
        arguments.add(new CharacterListArgument("characters", null, false, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new OutputDescribeDirectiveInvocation();
    }
    
}
