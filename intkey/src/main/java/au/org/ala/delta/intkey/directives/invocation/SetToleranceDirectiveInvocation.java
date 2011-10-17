package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetToleranceDirectiveInvocation extends IntkeyDirectiveInvocation {
    
    private int _toleranceValue;
    

    public void setToleranceValue(int toleranceValue) {
        this._toleranceValue = toleranceValue;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setTolerance(_toleranceValue);
        return true;
    }

    @Override
    public String toString() {
        return String.format("SET TOLERANCE %s", _toleranceValue);
    }
    
}
