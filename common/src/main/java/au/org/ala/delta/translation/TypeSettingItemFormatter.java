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

import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Extends the ItemFormatter to apply typesetting marks to the item description as appropriate.
 */
public class TypeSettingItemFormatter extends ItemFormatter {

	private ItemListTypeSetter _typeSetter;
	
	public TypeSettingItemFormatter(ItemListTypeSetter typeSetter) {
		this(typeSetter, false);
	}	
	
	public TypeSettingItemFormatter(ItemListTypeSetter typeSetter, boolean includeNumber) {
		this(typeSetter, includeNumber, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN);
	}

	public TypeSettingItemFormatter(ItemListTypeSetter typeSetter, boolean includeNumber, CommentStrippingMode commentMode, AngleBracketHandlingMode angleMode) {
		super(includeNumber, commentMode, angleMode, false, false, false);
		_typeSetter = typeSetter;
	}
	
	@Override
	public String defaultFormat(String text) {
		
		String formatted = super.defaultFormat(text);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
	@Override
	public String defaultFormat(String text, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
		
		String formatted = super.defaultFormat(text, commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
}
