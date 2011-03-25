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

	public MultiStateCharacter(int number, CharacterType characterType) {
		super(number, characterType);
	}

	public int getNumberOfStates() {
		return _impl.getNumberOfStates();
	}

	public void setNumberOfStates(int states) {
		_impl.setNumberOfStates(states);
		notifyObservers();
	}

	public void setState(int stateNumber, String text) {
		_impl.setStateText(stateNumber, text);
		notifyObservers();
	}

	public String getState(int stateNumber) {
		return stateNumber + ". " + _impl.getStateText(stateNumber);
	}

	public String[] getStates() {
		String[] states = new String[_impl.getNumberOfStates()];
		for (int i = 1; i <= states.length; i++) {
			states[i - 1] = _impl.getStateText(i);
		}
		return states;
	}

	public int getCodedImplicitState() {
		return _impl.getCodedImplicitState();
	}
	
	public int getUncodedImplicitState() {
		return _impl.getUncodedImplicitState();
	}
	
	public void setCodedImplicitState(int stateId) {
		_impl.setCodedImplicitState(stateId);
		notifyObservers();
	}
	
	public void setUncodedImplicitState(int stateId) {
		_impl.setUncodedImplicitState(stateId);
		notifyObservers();
	}

}
