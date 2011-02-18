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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class CharacterDependency {

	private int _controllingCharacterId;
	private int _dependentCharacterId;
	private Set<Integer> _states = new HashSet<Integer>();

	public CharacterDependency(int controllingCharacterId, Set<Integer> states, int dependentCharacterId) {
		_controllingCharacterId = controllingCharacterId;
		_dependentCharacterId = dependentCharacterId;
		_states = states;
	}

	public int getControllingCharacterId() {
		return _controllingCharacterId;
	}

	public int getDependentCharacterId() {
		return _dependentCharacterId;
	}

	public void addStateValueId(int stateId) {
		_states.add(stateId);
	}

	public Set<Integer> getStates() {
		return _states;
	}

	@Override
	public String toString() {
		String states = StringUtils.join(_states, ", ");
		return String.format("Char. %d controls char. %d for states [%s]", _controllingCharacterId, _dependentCharacterId, states);
	}
}
