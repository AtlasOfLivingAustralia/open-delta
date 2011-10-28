package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Utils;

public class OutputTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        List<Integer> taxonNumbers = new ArrayList<Integer>();
        for (Item taxon : _taxa) {
            taxonNumbers.add(taxon.getItemNumber());
        }

        try {
            if (context.getLastOutputLineWasComment()) {
                context.setLastOutputLineWasComment(false);
                context.appendToOutputFile(Utils.formatIntegersAsListOfRanges(taxonNumbers));
            } else {
                context.appendToOutputFile(String.format("OUTPUT TAXA %s", Utils.formatIntegersAsListOfRanges(taxonNumbers)));
            }
        } catch (IllegalStateException ex) {
            throw new IntkeyDirectiveInvocationException("NoOutputFileOpen.error");
        }

        return true;
    }

}
