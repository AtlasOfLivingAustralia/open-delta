package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayInapplicablesDirectiveInvocation implements IntkeyDirectiveInvocation {

    private boolean _value;

    public void setValue(boolean value) {
        this._value = value;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        context.setDisplayInapplicables(_value);
        return true;
    }

}
