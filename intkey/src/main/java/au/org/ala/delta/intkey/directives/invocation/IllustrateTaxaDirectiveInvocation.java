package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class IllustrateTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        //filter out any taxa that do not have images
        List<Item> taxaWithoutImages = new ArrayList<Item>();
        for (Item taxon: _taxa) {
            if (taxon.getImageCount() == 0) {
                taxaWithoutImages.add(taxon);
            }
        }
        
        _taxa.removeAll(taxaWithoutImages);
        
        if (_taxa.isEmpty()) {
            context.getUI().displayErrorMessage("No images for the specified taxa");
            return false;
        }
        
        context.getUI().IllustrateTaxa(_taxa);
        return true;
    }

}
