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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Creates formatter classes using the formatting options supplied in the
 * DeltaContext.
 */
public class FormatterFactory {
	
	private DeltaContext _context;
	
	public FormatterFactory(DeltaContext context) {
		_context = context;
	}
	
	public ItemFormatter createItemFormatter(ItemListTypeSetter typeSetter, boolean includeNumber) {
		CommentStrippingMode mode = CommentStrippingMode.RETAIN;
		if (_context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}
		return createItemFormatter(typeSetter, mode, includeNumber);
	}
	
	public ItemFormatter createItemFormatter(ItemListTypeSetter typeSetter) {
		return createItemFormatter(typeSetter, CommentStrippingMode.RETAIN, false);
	}
	
	public ItemFormatter createItemFormatter(ItemListTypeSetter typeSetter, CommentStrippingMode mode, boolean includeNumber) {
		ItemFormatter formatter = null;
		AngleBracketHandlingMode angleBracketMode = AngleBracketHandlingMode.RETAIN;
		if (_context.isReplaceAngleBrackets()) {
			angleBracketMode = AngleBracketHandlingMode.REMOVE;
		}
		if (_context.isOmitTypeSettingMarks()) {
			formatter = new ItemFormatter(includeNumber, mode, angleBracketMode, true, false, false);
		}
		else if (typeSetter == null) {
			formatter = new ItemFormatter(includeNumber, mode, angleBracketMode, false, false, false);
		}
		else {
			formatter = new TypeSettingItemFormatter(typeSetter, includeNumber,  mode, angleBracketMode);
		}
		setFormattingOptions(formatter);
			
		return formatter;
	}
	
	public CharacterFormatter createCharacterFormatter() {
		return createCharacterFormatter(false, false);
	}
	
	public CharacterFormatter createCharacterFormatter(boolean includeNumber, boolean capitaliseFirst) {
		CommentStrippingMode mode = CommentStrippingMode.STRIP_ALL;
		if (_context.getTranslateType() == TranslateType.IntKey) {
			if (_context.getOmitInnerComments()) {
				mode = CommentStrippingMode.STRIP_INNER;
			}
		}
		return createCharacterFormatter(includeNumber, capitaliseFirst, mode);
	}
	
	public CharacterFormatter createCharacterFormatter(boolean includeNumber, boolean capitaliseFirst, CommentStrippingMode mode) {
		
		AngleBracketHandlingMode angleBracketMode = AngleBracketHandlingMode.RETAIN;
		if (_context.isReplaceAngleBrackets()) {
			angleBracketMode = AngleBracketHandlingMode.CONTEXT_SENSITIVE_REPLACE;
		}
		
		if (_context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}

		CharacterFormatter formatter =  new CharacterFormatter(includeNumber, mode, angleBracketMode, _context.isOmitTypeSettingMarks(), capitaliseFirst);
		setFormattingOptions(formatter);
		
		return formatter;
	}
	
	public AttributeFormatter createAttributeFormatter() {
		AttributeFormatter formatter = null;
		AngleBracketHandlingMode angleBracketMode = AngleBracketHandlingMode.RETAIN;
		if (_context.isReplaceAngleBrackets()) {
			angleBracketMode = AngleBracketHandlingMode.CONTEXT_SENSITIVE_REPLACE;
		}
		CommentStrippingMode mode = CommentStrippingMode.RETAIN;
		if (_context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}
		if (_context.isOmitTypeSettingMarks()) {
			formatter = new AttributeFormatter(false, true, mode, angleBracketMode, false, null);
		}
		else {
            TypeSettingMark mark = _context.getTypeSettingMark(MarkPosition.RANGE_SYMBOL);

            String numericSeparator = mark != null ? mark.getMarkText() : "\\endash{}";
			formatter = new TypeSettingAttributeFormatter(numericSeparator, mode, angleBracketMode);
		}
		setFormattingOptions(formatter);
		return formatter;
	}
	
	protected void setFormattingOptions(Formatter formatter) {
		formatter.setRtfToHtml(_context.getOutputHtml());
		TypeSettingMark mark = _context.getTypeSettingMark(MarkPosition.RANGE_SYMBOL);
		if (mark != null) {
			formatter.setDashReplacement(mark.getMarkText());
		}
	}
}
