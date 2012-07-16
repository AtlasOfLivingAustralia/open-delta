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

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetVaryWtDirectiveInvocation extends BasicIntkeyDirectiveInvocation {
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
            if (!context.isProcessingDirectivesFile()) {
                context.clearBestOrSeparateCharacters();
                context.getUI().handleUpdateAll();
            }
        } else {
            context.getUI().displayErrorMessage("Value out of range. A valid value is a real number in the range 0-1.");
        }
        return true;
    }
}
