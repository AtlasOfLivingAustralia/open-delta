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
import au.org.ala.delta.model.attribute.AttrChunk;
import au.org.ala.delta.model.attribute.ChunkType;
import au.org.ala.delta.model.attribute.ParsedAttribute;
import au.org.ala.delta.model.impl.ControllingInfo;

/**
 * Delegates to the model classes to validate an attribute of an item.
 */
public class AttributeValidator implements Validator {

    private static final String INAPPLICABLE_ERROR_CODE = "EAP_IS_INAPPLICABLE";

	/** The attribute we are validating the text for */
	private Attribute _attribute;	
	private EditorViewModel _model;
    /** Whether or not this attribute has been found to be inapplicable */
	private ControllingInfo _controlled;
	/**
	 * Creates an AttributeValidator capable of validating an attribute identified by the
	 * supplied Item and Character.
	 * 
	 * @param dataset The dataset the attribute belongs to
	 * @param attribute The attribute the text will be validated on behalf of.
     * @param controlled Whether or not this attribute has been found to be inapplicable
	 */
	public AttributeValidator(EditorViewModel dataset, Attribute attribute, ControllingInfo controlled) {
		_attribute = attribute;
		_model = dataset;
        _controlled = controlled;
	}
	
	/**
	 * Delegates to the model classes to perform the validation and converts an Exception into
	 * an appropriate message.
	 */
	public ValidationResult validate(Object attributeText) {
		ValidationResult result = null;
		
		String text = _model.attributeValueFromDisplayText(_attribute, (String)attributeText);
		
		try { 
			_attribute.getCharacter().validateAttributeText(text, _controlled);
			result = new ValidationResult();
		} catch (AttributeParseException e) {
			result = new ValidationResult(e.getMessage(), e.getValue());
		}
		
		return result;
	}

}
