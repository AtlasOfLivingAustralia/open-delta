package au.org.ala.delta.intkey.directives;

public class IntkeyDirectiveFlag {

    private char _symbol;
    private String _name;
    
    private boolean _defaultValue;

    public IntkeyDirectiveFlag(char symbol, String name, boolean defaultValue) {
        this._symbol = symbol;
        this._name = name;
        this._defaultValue = defaultValue;
    }

    public char getSymbol() {
        return _symbol;
    }

    public String getName() {
        return _name;
    }
    
    public boolean getDefaultValue() {
        return _defaultValue;
    }

}
