package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public abstract class OnOffDirective extends NewIntkeyDirective {
    
    public OnOffDirective(boolean errorIfNoDatasetLoaded, String... controlWords) {
        super(errorIfNoDatasetLoaded, controlWords);
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new OnOffArgument("value", null, context.displayComments()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected abstract IntkeyDirectiveInvocation buildCommandObject();
}
