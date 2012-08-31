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
package au.org.ala.delta.model;


import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.impl.AttributeData;

public class RealAttribute extends NumericAttribute {

    public RealAttribute(RealCharacter character, AttributeData impl) {
        super(character, impl);
    }
    
    @Override
    public RealCharacter getCharacter() {
        return (RealCharacter) super.getCharacter();
    }

    /**
     * An implicit value is one for which no attribute value is coded but an implicit value
     * has been specified for the attributes character.
     * @return true if the value of this attribute is derived from the Characters implicit value.
     */
    public boolean isImplicit() {
        return false;
    }
    
    public FloatRange getPresentRange() {
        return _impl.getRealRange();
    }
    
    public void setPresentRange(FloatRange range) {
        _impl.setRealRange(range);
    }

}
