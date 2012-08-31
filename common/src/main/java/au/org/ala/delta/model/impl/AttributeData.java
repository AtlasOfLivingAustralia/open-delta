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
package au.org.ala.delta.model.impl;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.attribute.ParsedAttribute;
import org.apache.commons.lang.math.FloatRange;

import java.util.List;
import java.util.Set;

/**
 * The AttributeData interface is used by the Attribute class (and subclasses) to access attribute data from
 * storage.
 * It's purpose is to provide the main model classes with access to both in-memory and on-disk data storage
 * implementations.  (There is currently a fair bit of non data retrieval logic contained in the implementations
 * of this interface due to the port that ideally could be refactored into the main Attribute classes).
 */
public interface AttributeData {
	
	public String getValueAsString();
	
	public void setValueFromString(String value) throws DirectiveException;
	
	public boolean isStatePresent(int stateNumber);
	
	public boolean isSimple();

	public void setStatePresent(int stateNumber, boolean present);
	
	public boolean isUnknown();
	
    public boolean isVariable();
	
	public boolean isInapplicable();
	
	public boolean isExclusivelyInapplicable(boolean ignoreComments);
	
	public FloatRange getRealRange();
	public void setRealRange(FloatRange range);
	
	public Set<Integer> getPresentStateOrIntegerValues();
	
	public void setPresentStateOrIntegerValues(Set<Integer> values);
	
	public boolean hasValueSet();

	public boolean isRangeEncoded();

	boolean isCommentOnly();

	public List<NumericRange> getNumericValue();

	boolean isCodedUnknown();

	List<Integer> getPresentStatesAsList();

	public ParsedAttribute parsedAttribute();

    boolean isInherited();
}
