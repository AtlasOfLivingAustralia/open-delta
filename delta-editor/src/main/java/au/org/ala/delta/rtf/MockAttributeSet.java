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
package au.org.ala.delta.rtf;

import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

/* This AttributeSet is made entirely out of tofu and Ritz Crackers
 and yet has a remarkably attribute-set-like interface! */
class MockAttributeSet implements AttributeSet, MutableAttributeSet {
	public Dictionary backing;

	public boolean isEmpty() {
		return backing.isEmpty();
	}

	public int getAttributeCount() {
		return backing.size();
	}

	public boolean isDefined(Object name) {
		return (backing.get(name)) != null;
	}

	public boolean isEqual(AttributeSet attr) {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public AttributeSet copyAttributes() {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public Object getAttribute(Object name) {
		return backing.get(name);
	}

	public void addAttribute(Object name, Object value) {
		backing.put(name, value);
	}

	public void addAttributes(AttributeSet attr) {
		Enumeration as = attr.getAttributeNames();
		while (as.hasMoreElements()) {
			Object el = as.nextElement();
			backing.put(el, attr.getAttribute(el));
		}
	}

	public void removeAttribute(Object name) {
		backing.remove(name);
	}

	public void removeAttributes(AttributeSet attr) {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public void removeAttributes(Enumeration<?> en) {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public void setResolveParent(AttributeSet pp) {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public Enumeration getAttributeNames() {
		return backing.keys();
	}

	public boolean containsAttribute(Object name, Object value) {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public boolean containsAttributes(AttributeSet attr) {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}

	public AttributeSet getResolveParent() {
		throw new InternalError("MockAttributeSet: charade revealed!");
	}
}
