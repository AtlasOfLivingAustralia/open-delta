package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDiagLevelInvocation implements IntkeyDirectiveInvocation {
    
    private int _diagLevel;

    public void setDiagLevel(int diagLevel) {
        this._diagLevel = diagLevel;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        context.setDiagLevel(_diagLevel);
        return true;
    }

}
