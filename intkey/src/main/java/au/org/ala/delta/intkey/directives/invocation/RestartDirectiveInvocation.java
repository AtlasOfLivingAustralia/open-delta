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

import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.MatchType;

public class RestartDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

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
        } else if (_queryParameters) {
            context.setMatchSettings(false, false, MatchType.OVERLAP);
            context.setDiagType(DiagType.TAXA);
        }

        context.restartIdentification();

        if (_zeroTolerance) {
            context.setTolerance(0);
        }

        // Write a message to the log
        int numIncludedTaxa = context.getIncludedTaxa().size();
        int numExcludedTaxa = context.getExcludedTaxa().size();

        if (_identificationParameters) {
            if (numExcludedTaxa > 0) {
                context.appendToLog(UIUtils.getResourceString("NewIdentificationTaxaExcluded.log", numIncludedTaxa));
            } else {
                context.appendToLog(UIUtils.getResourceString("NewIdentification.log", numIncludedTaxa));
            }
        } else if (_queryParameters) {
            if (numExcludedTaxa > 0) {
                context.appendToLog(UIUtils.getResourceString("NewQueryTaxaExcluded.log", numIncludedTaxa));
            } else {
                context.appendToLog(UIUtils.getResourceString("NewQuery.log", numIncludedTaxa));
            }
        } else {
            if (numExcludedTaxa > 0) {
                context.appendToLog(UIUtils.getResourceString("SpecimenDataClearedTaxaExcluded.log", numIncludedTaxa));
            } else {
                context.appendToLog(UIUtils.getResourceString("SpecimenDataCleared.log", numIncludedTaxa));
            }
        }

        return true;
    }
}
