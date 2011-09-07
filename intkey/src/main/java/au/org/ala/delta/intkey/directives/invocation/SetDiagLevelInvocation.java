package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDiagLevelInvocation implements IntkeyDirectiveInvocation {
    
    private int _diagLevel;
    private boolean _fakeFlag;

    public void setDiagLevel(int diagLevel) {
        this._diagLevel = diagLevel;
    }
    
    public void setFakeFlag(boolean fakeFlag) {
        this._fakeFlag = fakeFlag;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setDiagLevel(_diagLevel);
        return true;
    }

}
