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
package au.org.ala.delta.model.impl;

public class ControllingInfo {
	
	public enum ControlledStateType {
		NotControlled, Inapplicable, InapplicableOrUnknown, MaybeInapplicable
	}
	
	private ControlledStateType _state;
	private int _controllingCharacterNo;
	
	public ControllingInfo() {
		_state = ControlledStateType.NotControlled;
		_controllingCharacterNo = 0;		
	}
	
	public ControllingInfo(ControlledStateType state, int controllingCharNo) {
		_state = state;
		_controllingCharacterNo = controllingCharNo;
	}
	
	public ControlledStateType getControlledState() {
		return _state;
	}
	
	public int getControllingCharacter() {
		return _controllingCharacterNo;
	}
	
	public boolean isStrictlyInapplicable() {
		return _state == ControlledStateType.Inapplicable;
	}
	
	public boolean isInapplicable() {
		return _state == ControlledStateType.Inapplicable || _state == ControlledStateType.InapplicableOrUnknown;
	}
	
	/**
	 * A Character is "maybe inapplicable" if the controlling attribute takes
	 * one of the values that render the Character inapplicable, but also 
	 * another value.
	 * For example, given the directive DEPENDENT CHARACTERS 10,2:11
	 * if for a given item 10,2 would render character 11 inapplicable
	 * but 10,1/2 would render character 11 maybe inapplicable.  (This
	 * distinction is used by CONFOR when translating into intkey and nexus 
	 * format. 
	 * @return true if the result of the applicability check was maybe inapplicable.
	 */
	public boolean isMaybeInapplicable() {
		return _state == ControlledStateType.MaybeInapplicable;
	}

}
