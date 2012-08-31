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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.RTFUtils;

/**
 * Validates an Item.
 */
public class ItemValidator extends DescriptionValidator implements Validator {

	private static final String NO_ITEM_DESCRIPTION = "EMPTY_ITEM_DESCRIPTION";

	/**
	 * Ensures that the item description is not empty.
	 */
	@Override
	public ValidationResult validate(Object toValidate) {

		String unformattedDescription = RTFUtils.stripFormatting((String) toValidate).trim();
		ValidationResult result = null;
		if (StringUtils.isEmpty(unformattedDescription)) {
			result = new ValidationResult(NO_ITEM_DESCRIPTION, 0);
		} else {
			result = validateComments(unformattedDescription);
		}

		return result;
	}

}
