package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Item;

public class DefineNamesDirectiveInvocation implements IntkeyDirectiveInvocation {

    private String _keyword;
    private List<String> _taxonNames;

    public DefineNamesDirectiveInvocation(String keyword, List<String> taxonNames) {
        _keyword = keyword;
        _taxonNames = taxonNames;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        IntkeyDataset dataset = context.getDataset();

        Set<Integer> taxaNumbers = new HashSet<Integer>();

        for (String taxonName : _taxonNames) {
            Item taxon = dataset.getTaxonByName(taxonName);
            if (taxon == null) {
                context.getUI().displayErrorMessage(String.format("'%s' is not a valid taxon name", taxonName));
                return false;
            } else {
                taxaNumbers.add(taxon.getItemNumber());
            }
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
