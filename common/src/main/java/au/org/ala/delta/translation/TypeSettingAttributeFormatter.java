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
package au.org.ala.delta.translation;

import au.org.ala.delta.model.format.AttributeFormatter;

/**
 * Extends the AttributeFormatter to recognise numeric ranges in comments
 * and format the separator correctly.
 */
public class TypeSettingAttributeFormatter extends AttributeFormatter {

	private static final String DEFAULT_RANGE_SEPARATOR = "\u2013";
	
	private String _numericRangeSeparator;
	
	public TypeSettingAttributeFormatter() {
		this(DEFAULT_RANGE_SEPARATOR);
	}
	
	public TypeSettingAttributeFormatter(String numericRangeSeparator) {
		super(false, false, CommentStrippingMode.RETAIN);
		_numericRangeSeparator = numericRangeSeparator;
	}
	
	public TypeSettingAttributeFormatter(String numericRangeSeparator, CommentStrippingMode commentMode, AngleBracketHandlingMode angleMode) {
		super(false, false, commentMode, angleMode);
		_numericRangeSeparator = numericRangeSeparator;
	}

	@Override
	public String formatComment(String comment) {
		comment = super.formatComment(comment);
		if (comment.startsWith("-")) {
			comment = _numericRangeSeparator+comment.substring(1);
		}
		return comment;
	}
}
