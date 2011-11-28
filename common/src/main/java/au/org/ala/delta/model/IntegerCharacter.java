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

public class IntegerCharacter extends NumericCharacter<Integer> {

	public IntegerCharacter(int number) {
		super(number, CharacterType.IntegerNumeric);
	}

    public int getMinimumValue() {
        return _impl.getMinimumValue();
    }

    public void setMinimumValue(int minimumValue) {
        _impl.setMinimumValue(minimumValue);
    }

    public int getMaximumValue() {
        return _impl.getMaximumValue();
    }

    public void setMaximumValue(int maximumValue) {
        _impl.setMaximumValue(maximumValue);
    }

}
