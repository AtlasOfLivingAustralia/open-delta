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

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.attribute.ParsedAttribute;
import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.model.observer.AttributeObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * An attribute is the value of a single character associated with an item.
 */
public abstract class Attribute {

    public static class Delimiters {
		public static char LITERAL = '|'; // Treat next character as literal;
											// not yet implemented.
		public static char HEX = '!'; // Treat next 2 characters as Hex byte;
										// not yet implemented.
		public static char DIRECTIVE = '*'; // Start of directive
		public static char ELEMSTART = '#'; // Start of character, item, etc.
		public static char ELEMEND = '/'; // End of character, item, etc.
		public static char OPENBRACK = '<'; // Opening bracket
		public static char CLOSEBRACK = '>'; // Closing bracket
		public static char KEYWORD = '@'; //
		public static char QUOTE = '\"'; // Quotation mark
		public static char SETDELIM = ':'; // Separates character numbers (or
											// ranges) in character sets
		public static char ORSTATE = '/'; // Separates "or"ed state values
		public static char ANDSTATE = '&'; // Separates "and"ed state values
		public static char STATERANGE = '-'; // Separates a range of values
	}

	public static final String INAPPICABLE = "-";
    public static final String VARIABLE = "V";
    public static final String UNKNOWN = "U";

    protected AttributeData _impl;

    protected Character _character;
    protected Item _item;
    private List<AttributeObserver> _observers;

    private boolean _isSpecimenAttribute = false;

    public Attribute(Character character, AttributeData impl) {
        _character = character;
        _impl = impl;
    }

    public Character getCharacter() {
        return _character;
    }

    public void setItem(Item item) {
        _item = item;
        if (_item != null) {
            _isSpecimenAttribute = false;
        }
    }

    public Item getItem() {
        return _item;
    }

    public boolean isSpecimenAttribute() {
        return _isSpecimenAttribute;
    }

    public void setSpecimenAttribute(boolean isSpecimenAttribute) {
        this._isSpecimenAttribute = isSpecimenAttribute;
        if (isSpecimenAttribute) {
            _item = null;
        }
    }

    /**
     * An unknown attribute is one that has not been coded, or has been coded
     * explicitly with the value "U".
     * Note that a variant Item or Characters with implicit values are not considered unknown.
     * 
     * @return true if the value of this attribute is unknown.
     */
    public boolean isUnknown() {
        return _impl.isUnknown();
    }

    /**
     * An unknown attribute is one that has has been coded explicitly with the
     * value "U".
     * 
     * @return true if the value of this attribute is unknown.
     */
    public boolean isCodedUnknown() {

        return _impl.isCodedUnknown();
    }

    /**
     * An inapplicable value is one that has been explicitly coded as
     * inapplicable - with the value "-"
     * 
     * @return true if this attribute is inapplicable
     */
    public boolean isInapplicable() {
        boolean inapplicable = _impl.isInapplicable();
        // if (!inapplicable) {
        // ControllingInfo result = _character.checkApplicability(getItem());
        // inapplicable = result.isInapplicable();
        // }
        return inapplicable;
    }

    /**
     * An exclusively inapplicable value is one that has been explicitly coded
     * as inapplicable with the value "-" and no other values.
     * 
     * @param ignoreComments
     *            if this parameter is true an attribute will be considered
     *            exclusively inapplicable if it is coded as inapplicable with a
     *            comment. Otherwise the comment will cause it to be not
     *            exclusively inapplicable.
     * @return true if this attribute is inapplicable
     */
    public boolean isExclusivelyInapplicable(boolean ignoreComments) {
        return _impl.isExclusivelyInapplicable(ignoreComments);
    }

    public boolean isExclusivelyInapplicable() {
        return _impl.isExclusivelyInapplicable(false);
    }

    public boolean isVariable() {
        return _impl.isVariable();
    }

    public boolean isSimple() {
        return _impl.isSimple();
    }

    public String getValueAsString() {
        return _impl.getValueAsString();
    }

    public void setValueFromString(String value) throws DirectiveException {
        _impl.setValueFromString(value);
        notifyObservers();
    }

    /**
     * @return true if this Attribute has no encoded data other than a comment.
     */
    public boolean isComment() {
        return _impl.isCommentOnly();
    }

    public ParsedAttribute parsedAttribute() {
    	return _impl.parsedAttribute();
    }
    
    /**
     * Registers interest in being notified of changes to this Attribute.
     * 
     * @param observer
     *            the object interested in receiving notification of changes.
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
     * @return true if this is an Attribute of a variant Item and the value of this Attribute has been inherited
     * from the master Item.
     */
    public boolean isInherited() {
        return _impl.isInherited();
    }

    /**
     * De-registers interest in changes to this Attribute.
     * 
     * @param observer
     *            the object no longer interested in receiving notification of
     *            changes.
     */
    public void removeAttributeObserver(AttributeObserver observer) {
        if (_observers == null) {
            return;
        }
        _observers.remove(observer);
    }

    /**
     * Notifies all registered CharacterObservers that this Character has
     * changed.
     */
    protected void notifyObservers() {
        if (_observers == null) {
            return;
        }
        // Notify observers in reverse order to support observer removal during
        // event handling.
        for (int i = _observers.size() - 1; i >= 0; i--) {
            _observers.get(i).attributeChanged(this);
        }
    }

}
