package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class ExcludeTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    private Set<Integer> _excludedTaxaNumbers;

    public void setTaxa(List<Item> taxa) {
        _excludedTaxaNumbers = new HashSet<Integer>();
        for (Item taxon : taxa) {
            _excludedTaxaNumbers.add(taxon.getItemNumber());
        }
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setExcludedTaxa(_excludedTaxaNumbers);
        return true;
    }
}
