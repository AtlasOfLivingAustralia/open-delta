/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.MatchType;

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
            context.setMatchSettings(true, true, MatchType.OVERLAP);
            context.setDiagType(DiagType.SPECIMENS);
        }
        
        if (_queryParameters) {
            context.setMatchSettings(false, false, MatchType.OVERLAP);
            context.setDiagType(DiagType.TAXA);
        }
        
        context.restartIdentification();
        
        if (_zeroTolerance) {
            context.setTolerance(0);
        }
        
        return true;
    }
}
