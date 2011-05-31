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

import java.util.Set;

import au.org.ala.delta.model.impl.CharacterDependencyData;

/**
 * Describes the relationship between a set of controlling characters and its dependent characters
 */
public class CharacterDependency {

	private CharacterDependencyData _impl;

	/**
	 * constructor
	 * @param controllingCharacterId id of the controlling character
	 * @param states the ids of the states which when set on the controlling character, make the dependent characters <b>inapplicable</b>
	 * @param dependentCharacterIds ids of the dependent characters
	 */
	public CharacterDependency(CharacterDependencyData impl) {
		_impl = impl;
	}
	
	public CharacterDependencyData getImpl() {
		return _impl;
	}

	/** 
	 * @return the id of the controlling character
	 */
	public int getControllingCharacterId() {
		return _impl.getControllingCharacterId();
	}

	/**
	 * @return the ids of the dependent characters
	 */
	public Set<Integer>  getDependentCharacterIds() {
	   return _impl.getDependentCharacterIds();
	}

	/**
	 * @return the states which when set on the controlling character, make the dependent characters <b>inapplicable</b>
	 */
	public Set<Integer> getStates() {
	    return _impl.getStates();
	}
	
	/**
	 * @return a description of this CharacterDependency.
	 */
	public String getDescription() {
		return _impl.getDescription();
	}
	
	/**
	 * Provides a description of this CharacterDependency.
	 * @param description a description of this CharacterDependency.
	 */
	public void setDescription(String description) {
		_impl.setDescription(description);
	}

	public void addDependentCharacter(Character toAdd) {
		
		if (getControllingCharacterId() == toAdd.getCharacterId()) {
			throw new CircularDependencyException();
		}
		if (toAdd.getCharacterType().isMultistate()) {
			MultiStateCharacter multiStateChar = (MultiStateCharacter)toAdd;
			if (multiStateChar.getControlledCharacterNumbers(true).contains(getControllingCharacterId())) {
				throw new CircularDependencyException();
			}
		}
		
		_impl.addDependentCharacter(toAdd);
		
		toAdd.addControllingCharacters(this);
	}
	
	public void removeDependentCharacter(Character toRemove) {
		_impl.removeDependentCharacter(toRemove);
		
		//toRemove.removeControllingAttribute(this);
		
	}
}
