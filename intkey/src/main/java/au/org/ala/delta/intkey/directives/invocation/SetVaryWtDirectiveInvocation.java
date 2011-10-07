package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetVaryWtDirectiveInvocation extends IntkeyDirectiveInvocation {
    private double _varyWt;

    public void setVaryWt(double varyWt) {
        this._varyWt = varyWt;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        if (_varyWt >= 0.0 && _varyWt <= 1.0) {
            context.setVaryWeight(_varyWt);
            // Clear the cached best characters then force the UI to update
            // itself,
            // calculating the best
            // characters in the process
            context.clearBestOrSeparateCharacters();
            context.getUI().handleUpdateAll();
        } else {
            context.getUI().displayErrorMessage("Value out of range. A valid value is a real number in the range 0-1.");
        }
        return true;
    }
}
