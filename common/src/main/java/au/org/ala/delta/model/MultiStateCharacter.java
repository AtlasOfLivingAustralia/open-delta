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

public abstract class MultiStateCharacter extends Character {

	private int _numberOfStates;
	private String[] _states;
	private ImplicitValue _implicitValueStateId;

	public MultiStateCharacter(int number, CharacterType characterType) {
		super(number, characterType);
	}

	public int getNumberOfStates() {
		return _impl.getNumberOfStates();
	}

	public void setNumberOfStates(int states) {
		_numberOfStates = states;
		_states = new String[_numberOfStates];
	}

	public String getState(int stateNumber) {
		return stateNumber + ". " + _impl.getStateText(stateNumber);
	}
	public String[] getStates() {
		return _states;
	}

	public void setImplicitValueStateId(ImplicitValue value) {
		_implicitValueStateId = value;
	}

	public ImplicitValue getImplicitValueStateId() {
		return _implicitValueStateId;
	}

	public void setState(int stateId, String state) {
		// TODO bounds check!
		_states[stateId - 1] = state;
	}

}
