package au.org.ala.delta.intkey.model.ported;

//See PARS.H
public class Params {
    
    private static boolean _ReUseLastChar = false;

    public static boolean isReUseLastChar() {
        return _ReUseLastChar;
    }

    public static void setReUseLastChar(boolean reUseLastChar) {
        _ReUseLastChar = reUseLastChar;
    }
    
    private static int _TaxonImageChar;

    public static int getTaxonImageChar() {
        return _TaxonImageChar;
    }

    public static void setTaxonImageChar(int _TaxonImageChar) {
        Params._TaxonImageChar = _TaxonImageChar;
    }
    
}
