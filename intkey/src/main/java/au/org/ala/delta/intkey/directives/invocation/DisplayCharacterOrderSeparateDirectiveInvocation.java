package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayCharacterOrderSeparateDirectiveInvocation extends IntkeyDirectiveInvocation {

    private int _taxonNumber;
    
    public void setTaxonNumber(int taxonNumber) {
        this._taxonNumber = taxonNumber;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setCharacterOrderSeparate(_taxonNumber);
        return true;
    }

}
