package au.org.ala.delta.intkey.directives.invocation;

import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class ExcludeTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    private Set<Integer> _excludedTaxaNumbers;

    public ExcludeTaxaDirectiveInvocation(Set<Integer> excludedTaxaNumbers) {
        _excludedTaxaNumbers = excludedTaxaNumbers;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setExcludedTaxa(_excludedTaxaNumbers);
        return true;
    }
}
