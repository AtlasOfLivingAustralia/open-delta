package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class DefineTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    public static final String LABEL = "DEFINE TAXA";

    private String keyword;
    private List<Item> taxa;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setTaxa(List<Item> taxa) {
        this.taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> taxaNumbers = new HashSet<Integer>();
        for (Item taxon : taxa) {
            taxaNumbers.add(taxon.getItemNumber());
        }

        context.addTaxaKeyword(keyword, taxaNumbers);
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", LABEL, keyword, StringUtils.join(taxa, " "));
    }

}
