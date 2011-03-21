package au.org.ala.delta.intkey;

//See PARS.H
public class Params {
    
    private static boolean _ReUseLastChar = false;

    public static boolean isReUseLastChar() {
        return _ReUseLastChar;
    }

    public static void setReUseLastChar(boolean reUseLastChar) {
        _ReUseLastChar = reUseLastChar;
    }
    
}
