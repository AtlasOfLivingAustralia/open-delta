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
            // Clear the cached best characters then force the UI to update
            // itself,
            // calculating the best
            // characters in the process
            if (!context.isProcessingInputFile()) {
                context.clearBestOrSeparateCharacters();
                context.getUI().handleUpdateAll();
            }
        } else {
            context.getUI().displayErrorMessage("Value out of range. A valid value is a real number in the range 1-5.");
        }
        return true;
    }
}
