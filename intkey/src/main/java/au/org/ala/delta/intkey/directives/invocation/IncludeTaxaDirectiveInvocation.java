package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class IncludeTaxaDirectiveInvocation implements IntkeyDirectiveInvocation {
    
    private List<Item> _taxa;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> includedTaxaNumbers = new HashSet<Integer>();
        for (Item taxon: _taxa) {
            includedTaxaNumbers.add(taxon.getItemNumber());
        }
        context.setIncludedTaxa(includedTaxaNumbers);
        return true;
    }
}
