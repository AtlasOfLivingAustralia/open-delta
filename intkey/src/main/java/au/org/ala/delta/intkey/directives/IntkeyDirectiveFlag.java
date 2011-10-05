package au.org.ala.delta.intkey.directives;

public class IntkeyDirectiveFlag {

    private char _symbol;
    private String _name;

    public IntkeyDirectiveFlag(char symbol, String name) {
        this._symbol = symbol;
        this._name = name;
    }

    public char getSymbol() {
        return _symbol;
    }

    public String getName() {
        return _name;
    }
}
