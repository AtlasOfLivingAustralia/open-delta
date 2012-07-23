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

import java.util.List;

public class RealCharacter extends NumericCharacter<Double> {

    public RealCharacter() {
        super(CharacterType.RealNumeric);
    }

    public List<Float> getKeyStateBoundaries() {
        return _impl.getKeyStateBoundaries();
    }

    public void setKeyStateBoundaries(List<Float> keyStateBoundaries) {
        _impl.setKeyStateBoundaries(keyStateBoundaries);
    }

    public boolean isIntegerRepresentedAsReal() {
        return _impl.isIntegerRepresentedAsReal();
    }

    public void setIntegerRepresentedAsReal(boolean isIntegerRepresentedAsReal) {
        _impl.setIntegerRepresentedAsReal(isIntegerRepresentedAsReal);
    }
}
