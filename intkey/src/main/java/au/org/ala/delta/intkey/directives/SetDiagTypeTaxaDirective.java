package au.org.ala.delta.intkey.directives;

import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetDiagTypeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDiagTypeTaxaDirective extends NewIntkeyDirective {

    public SetDiagTypeTaxaDirective() {
        super(false, "set", "diagtype", "taxa");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetDiagTypeTaxaDirectiveInvocation();
    }

}
