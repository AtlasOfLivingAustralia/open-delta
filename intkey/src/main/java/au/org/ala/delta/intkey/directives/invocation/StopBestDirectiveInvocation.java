package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class StopBestDirectiveInvocation extends IntkeyDirectiveInvocation {

    private int _value;
    
    public void setValue(int value) {
        this._value = value;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setStopBest(_value);
        return true;
    }

}
