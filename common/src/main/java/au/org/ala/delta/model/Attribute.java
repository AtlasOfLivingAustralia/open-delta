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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.model.observer.AttributeObserver;

/**
 * An attribute is the value of a single character associated with an item.
 */
public class Attribute {

	private AttributeData _impl;
	
	protected Character _character;
	protected Item _item;
	private List<AttributeObserver> _observers;
	
	public Attribute(Character character, AttributeData impl) {
		_character = character;
		_impl = impl;
	}
	
	public Character getCharacter() {
		return _character;
	}
	
	public void setItem(Item item) {
		_item = item;
	}
	
	public Item getItem() {
		return _item;
	}
	
	public boolean isPresent(int stateNumber) {
		
		if (!(_character instanceof MultiStateCharacter)) {
			return false;
		}
		
		
		boolean statePresent = _impl.isStatePresent(stateNumber);
		
		if ((statePresent == false) && (StringUtils.isEmpty(getValue()))) {
			MultiStateCharacter multiStateChar = (MultiStateCharacter)_character;
			statePresent = (stateNumber == multiStateChar.getUncodedImplicitState());
		}
		return statePresent;
	}
	
	public void setStatePresent(int stateNumber, boolean present) {
		if (!(_character instanceof MultiStateCharacter)) {
			return;
		}
		
		_impl.setStatePresent(stateNumber, present);
		
		notifyObservers();
	}
	
	/**
	 * An implicit value is one for which no attribute value is coded but an implicit value
	 * has been specified for the attributes character.
	 * @return true if the value of this attribute is derived from the Characters implicit value.
	 */
	public boolean isImplicit() {
		if (!(_character instanceof MultiStateCharacter)) {
			return false;
		}
		MultiStateCharacter multiStateChar = (MultiStateCharacter)_character;
		return (StringUtils.isEmpty(getValue()) && multiStateChar.getUncodedImplicitState() > 0);
	}
	
	/**
	 * An unknown attribute is one that has not been coded, or has been coded explicitly with 
	 * the value "U".
	 * @return true if the value of this attribute is unknown.
	 */
	public boolean isUnknown() {
		String value = getValue();
		return ("U".equals(value) || (StringUtils.isEmpty(value) && !isImplicit()));
	}
	
	public boolean isSimple() {
		return _impl.isSimple();
	}
	
	public String getValue() {
		return _impl.getValue();
	}
	
	public void setValue(String value) {
		_impl.setValue(value);
		notifyObservers();
	}
	
	
	/**
	 * Registers interest in being notified of changes to this Attribute.
	 * @param observer the object interested in receiving notification of changes.
	 */
	public void addAttributeObserver(AttributeObserver observer) {
		if (_observers == null) {
			_observers = new ArrayList<AttributeObserver>(1);
		}
		if (!_observers.contains(observer)) {
			_observers.add(observer);
		}
	}
	
	/**
	 * De-registers interest in changes to this Attribute.
	 * @param observer the object no longer interested in receiving notification of changes.
	 */
	public void removeAttributeObserver(AttributeObserver observer) {
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
			_observers.get(i).attributeChanged(this);
		}
	}

	/**
	 * @return the implicit value of this attribute.
	 */
	public String getImplicitValue() {
		if (!isImplicit()) {
			throw new RuntimeException("Cannot get an implict value on an attribute that is not implicit.");
		}
		MultiStateCharacter multiStateChar = (MultiStateCharacter)_character;
		int implicitState = multiStateChar.getUncodedImplicitState();
		return Integer.toString(implicitState);
	}
	
}
