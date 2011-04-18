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
	private Set<Integer> _dependentCharacterIds;
	private Set<Integer> _states = new HashSet<Integer>();

	public CharacterDependency(int controllingCharacterId, Set<Integer> states, Set<Integer> dependentCharacterIds) {
		_controllingCharacterId = controllingCharacterId;
		_dependentCharacterIds = new HashSet<Integer>(dependentCharacterIds);
		_states = new HashSet<Integer>(states);
	}

	public int getControllingCharacterId() {
		return _controllingCharacterId;
	}

	public Set<Integer>  getDependentCharacterIds() {
	    //return defensive copy
		return new HashSet<Integer>(_dependentCharacterIds);
	}

	public void addStateValueId(int stateId) {
		_states.add(stateId);
	}

	public Set<Integer> getStates() {
	    //return defensive copy
		return new HashSet<Integer>(_states);
	}

	@Override
	public String toString() {
		String states = StringUtils.join(_states, ", ");
		return String.format("Char. %d controls chars. [%s] for states [%s]", _controllingCharacterId, _dependentCharacterIds, states);
	}
}
