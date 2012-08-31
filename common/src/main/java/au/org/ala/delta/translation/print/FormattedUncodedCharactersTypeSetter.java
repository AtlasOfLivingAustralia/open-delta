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
package au.org.ala.delta.translation.print;

import java.util.Map;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.translation.PrintFile;

public class FormattedUncodedCharactersTypeSetter extends UncodedCharactersTypeSetter {

	private Map<Integer, TypeSettingMark> _typeSettingMarks;
	
	public FormattedUncodedCharactersTypeSetter(PrintFile printer, DeltaContext context) {
		super(printer);
		_typeSettingMarks = context.getTypeSettingMarks();
	}
	
	public void beforeUncodedCharacterList() {
		_printer.printBufferLine();
		writeTypeSettingMark(MarkPosition.BEFORE_LIST_OF_UNCODED_CHARACTERS);
	}
	
	
	public void beforeNewParagraph() {
		writeTypeSettingMark(MarkPosition.BEFORE_NEW_PARAGRAPH_CHARACTER);
	}
	
	public String rangeSeparator() {
		TypeSettingMark typesettingMark =_typeSettingMarks.get(MarkPosition.RANGE_SYMBOL.getId());
		if (typesettingMark != null) {
			return typesettingMark.getMarkText();
		}
		return super.rangeSeparator();
	}
	
	protected void writeTypeSettingMark(MarkPosition mark) {
		TypeSettingMark typesettingMark = _typeSettingMarks.get(mark.getId());
		if (typesettingMark != null) {
			_printer.writeTypeSettingMark(typesettingMark.getMarkText());
		}
	}
}
