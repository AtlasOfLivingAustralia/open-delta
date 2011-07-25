package au.org.ala.delta.intkey.directives.invocation;

import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class IncludeTaxaDirectiveInvocation implements IntkeyDirectiveInvocation {

    private Set<Integer> _includedTaxaNumbers;

    public IncludeTaxaDirectiveInvocation(Set<Integer> includedTaxaNumbers) {
        _includedTaxaNumbers = includedTaxaNumbers;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setIncludedTaxa(_includedTaxaNumbers);
        return true;
    }
}
