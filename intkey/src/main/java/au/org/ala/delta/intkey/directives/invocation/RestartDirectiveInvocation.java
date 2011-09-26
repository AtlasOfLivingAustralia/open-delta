package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;

public class RestartDirectiveInvocation extends IntkeyDirectiveInvocation {

    boolean _identificationParameters = false;
    boolean _queryParameters = false;
    boolean _zeroTolerance = false;
    
    public void setIdentificationParameters(boolean identificationParameters) {
        this._identificationParameters = identificationParameters;
    }

    public void setQueryParameters(boolean queryParameters) {
        this._queryParameters = queryParameters;
    }

    public void setZeroTolerance(boolean zeroTolerance) {
        this._zeroTolerance = zeroTolerance;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        
        if (_identificationParameters) {
            context.setMatchType(MatchType.OVERLAP);
            context.setMatchInapplicables(true);
            context.setMatchUnknowns(true);
            context.setDiagType(DiagType.SPECIMENS);
        }
        
        if (_queryParameters) {
            context.setMatchType(MatchType.OVERLAP);
            context.setMatchInapplicables(false);
            context.setMatchUnknowns(false);
            context.setDiagType(DiagType.TAXA);
        }
        
        context.restartIdentification();
        
        if (_zeroTolerance) {
            context.setTolerance(0);
        }
        
        return true;
    }
}
