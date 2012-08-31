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
package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.RealRangeValidator;
import au.org.ala.delta.directives.validation.RealValidator;
import au.org.ala.delta.key.KeyContext;

public class StorageFactorDirective extends AbstractRealDirective {

    public StorageFactorDirective() {
        super("storage", "factor");
    }
    
    @Override
    protected void processReal(KeyContext context, double value) throws Exception {
        // do nothing, this is a no-op directive, implemented only for backwards
        // compatibility
    }
    
    @Override
    protected RealValidator createValidator(DeltaContext context) {
        return null;
    }

}
