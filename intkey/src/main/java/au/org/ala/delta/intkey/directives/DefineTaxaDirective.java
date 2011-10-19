package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.invocation.DefineTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class DefineTaxaDirective extends NewIntkeyDirective {

    public DefineTaxaDirective() {
        super(true, "define", "taxa");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new StringArgument("keyword", "Enter keyword", null, true));
        arguments.add(new TaxonListArgument("taxa", null, SelectionMode.KEYWORD, false, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DefineTaxaDirectiveInvocation();
    }
}
