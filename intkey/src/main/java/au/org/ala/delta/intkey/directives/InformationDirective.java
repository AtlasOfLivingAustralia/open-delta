package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.InformationDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class InformationDirective extends NewIntkeyDirective {

    public InformationDirective() {
        super("information");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new TaxonListArgument("taxa", null, SelectionMode.KEYWORD, false, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('I', "imagesAutoDisplayText", true));
        flags.add(new IntkeyDirectiveFlag('O', "otherItemsAutoDisplayText", true));
        flags.add(new IntkeyDirectiveFlag('X', "closePromptAfterAutoDisplay", false));
        return flags;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new InformationDirectiveInvocation();
    }

}
