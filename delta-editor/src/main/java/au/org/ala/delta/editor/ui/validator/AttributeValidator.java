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
package au.org.ala.delta.editor.ui.validator;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.Attribute.AttributeParseException;
import au.org.ala.delta.model.Attribute;

/**
 * Delegates to the model classes to validate an attribute of an item.
 */
public class AttributeValidator implements Validator {

	/** The attribute we are validating the text for */
	private Attribute _attribute;	
	private EditorViewModel _model;
	
	/**
	 * Creates an AttributeValidator capable of validating an attribute identified by the
	 * supplied Item and Character.
	 * 
	 * @param dataset The dataset the attribute belongs to
	 * @param attribute The attribute the text will be validated on behalf of.
	 */
	public AttributeValidator(EditorViewModel dataset, Attribute attribute) {
		_attribute = attribute;
		_model = dataset;
	}
	
	/**
	 * Delegates to the model classes to perform the validation and converts an Exception into
	 * an appropriate message.
	 */
	public ValidationResult validate(Object attributeText) {
		ValidationResult result = null;
		
		String text = _model.attributeValueFromDisplayText(_attribute, (String)attributeText);
		
		try { 
			_attribute.getCharacter().validateAttributeText(text);
			result = new ValidationResult();
		} catch (AttributeParseException e) {
			result = new ValidationResult(e.getMessage(), e.getValue());
		}
		
		return result;
	}
}
