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

import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.model.observer.AttributeObserver;

/**
 * An attribute is the value of a single character associated with an item.
 */
public abstract class Attribute {

	protected AttributeData _impl;
	
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
	
	/**
	 * An unknown attribute is one that has not been coded, or has been coded explicitly with 
	 * the value "U".
	 * @return true if the value of this attribute is unknown.
	 */
	public boolean isUnknown() {
	    return _impl.isUnknown();
	}
	
	/**
	 * An inapplicable value is one that has been explicitly coded as inapplicable 
	 * - with the value "-"
	 * @return true if this attribute is inapplicable
	 */
	public boolean isInapplicable() {
	    return _impl.isInapplicable();
	}
	
	public boolean isSimple() {
		return _impl.isSimple();
	}
	
	public String getValueAsString() {
		return _impl.getValueAsString();
	}
	
	public void setValueFromString(String value) {
		_impl.setValueFromString(value);
		notifyObservers();
	}
	
	/**
	 * @return true if this Attribute has no encoded data other than a comment.
	 */
	public boolean isComment() {
		return _impl.isCommentOnly();
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
	
}
