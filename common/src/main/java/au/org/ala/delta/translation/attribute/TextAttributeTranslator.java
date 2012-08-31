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
package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.model.format.AttributeFormatter;




/**
 * The TextAttributeTranslator is responsible for translating TextCharacter attributes into 
 * natural language.
 */
public class TextAttributeTranslator extends AttributeTranslator {

	public TextAttributeTranslator(AttributeFormatter formatter, boolean omitOr) {
		super(formatter, omitOr);
	}
	
	@Override
	public String translateValue(String value) {
		throw new RuntimeException("This should never have been called");
	}

	@Override
	public String rangeSeparator() {
		return "";
	}
	
	/**
	 * Overrides the parent method to omit the brackets surrounding the comment.
	 */
	public String translateCharacterComment(String comment) {
		
		comment = _attributeFormatter.formatTextAttribute(comment);
		
		return comment;
	}

}
