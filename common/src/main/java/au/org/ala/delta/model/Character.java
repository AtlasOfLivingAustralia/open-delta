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

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.impl.CharacterData;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.model.observer.CharacterObserver;

public abstract class Character {
	
	private int _number;
	private List<CharacterDependency> _dependentCharacters = new ArrayList<CharacterDependency>();
	protected CharacterData _impl;	
	private CharacterType _characterType;
	private List<CharacterObserver> _observers;
	private double _reliability;
	
	protected Character(int number, CharacterType characterType) {
		_characterType = characterType;
		_number = number;
	}
	
	public CharacterType getCharacterType() {
		return _characterType;
	}
	
	public int getCharacterId() {
		return _number;
	}
	
	public String getDescription() {
		return _impl.getDescription();
	}
	
	public void setDescription(String desc) {
		_impl.setDescription(desc);
		notifyObservers();
	}
	
	public void addDependentCharacters(CharacterDependency dependency) {
		_dependentCharacters.add(dependency);
	}
	
	public List<CharacterDependency> getDependentCharacters() {
		return _dependentCharacters;
	}
	
	public void setMandatory(boolean b) {
		_impl.setMandatory(b);
		notifyObservers();
	}
	
	public boolean isMandatory() {
		return _impl.isMandatory();
	}
	
	public void setExclusive(boolean exclusive) {
		_impl.setExclusive(exclusive);
		notifyObservers();
	}
	
	public boolean isExclusive() {
		return _impl.isExclusive();
	}
	
	public void setNotes(String notes) {
		_impl.setNotes(notes);
		notifyObservers();
	}
	
	public String getNotes() {
		return _impl.getNotes();
	}
	
	@Override
	public String toString() {
		return _number + ". " + getDescription();
	}
	
	public CharacterData getImpl() {
		return _impl;
	}
	
	public void setImpl(CharacterData impl) {
		_impl = impl;
	}

	public void validateAttributeText(String text) {
		_impl.validateAttributeText(text);		
	}
	

	/**
	 * Registers interest in being notified of changes to this Character.
	 * @param observer the object interested in receiving notification of changes.
	 */
	public void addCharacterObserver(CharacterObserver observer) {
		if (_observers == null) {
			_observers = new ArrayList<CharacterObserver>(1);
		}
		if (!_observers.contains(observer)) {
			_observers.add(observer);
		}
	}
	
	/**
	 * De-registers interest in changes to this Character.
	 * @param observer the object no longer interested in receiving notification of changes.
	 */
	public void removeCharacterObserver(CharacterObserver observer) {
		if (_observers == null) {
			return;
		}
		_observers.remove(observer);
	}
	
	/**
	 * Notifies all registered CharacterObservers that this Character has changed.
	 */
	protected void notifyObservers() {
		if (_observers == null) {
			return;
		}
		// Notify observers in reverse order to support observer removal during event handling.
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).characterChanged(this);
		}
	}
	
	public ControllingInfo checkApplicability(Item item) {
		return _impl.checkApplicability(item);
	}
	
    public double getReliability() {
        return _impl.getReliability();
    }

    public void setReliability(double reliability) {
        _impl.setReliability(reliability);
    }
	
}
