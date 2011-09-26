package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetRBaseDirectiveInvocation extends IntkeyDirectiveInvocation {

    private double _rbase;

    public void setRbase(double rbase) {
        this._rbase = rbase;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        if (_rbase >= 1.0 && _rbase <= 5.0) {
            context.setRBase(_rbase);
        } else {
            context.getUI().displayErrorMessage("Value out of range. A valid value is a real number in the range 1-5.");
        }
        return true;
    }

}
