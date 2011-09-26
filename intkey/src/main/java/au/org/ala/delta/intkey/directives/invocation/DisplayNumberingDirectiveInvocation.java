package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayNumberingDirectiveInvocation extends IntkeyDirectiveInvocation {

    private boolean _value;

    public void setValue(boolean value) {
        this._value = value;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setDisplayNumbering(_value);
        return true;
    }

}
