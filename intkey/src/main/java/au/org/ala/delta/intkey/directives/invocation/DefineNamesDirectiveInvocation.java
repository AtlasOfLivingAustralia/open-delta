package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class DefineNamesDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _keyword;
    private List<Item> _taxa;

    public DefineNamesDirectiveInvocation(String keyword, List<Item> taxa) {
        _keyword = keyword;
        _taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> taxaNumbers = new HashSet<Integer>();

        for (Item taxon : _taxa) {
            taxaNumbers.add(taxon.getItemNumber());
        }

        try {
            context.addTaxaKeyword(_keyword, taxaNumbers);
        } catch (IllegalArgumentException ex) {
            context.getUI().displayErrorMessage(String.format("'%s' is a system keyword and cannot be redefined", _keyword));
            return false;
        }

        return true;
    }

}
