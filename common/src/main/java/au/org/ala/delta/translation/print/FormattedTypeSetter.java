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

import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.translation.PrintFile;

/**
 * Inserts typesetting marks (specified using the TYPESETTING MARKS directive)
 * into a character list produced using the PRINT CHARACTER LIST directive.
 */
public class FormattedTypeSetter extends PlainTextTypeSetter {

	/** The typesetting marks to use */
	private Map<Integer, TypeSettingMark> _marks;
	
	public FormattedTypeSetter(Map<Integer, TypeSettingMark> typeSettingMarks, PrintFile printer) {
		super(printer);
		_marks = typeSettingMarks;
	}
	
	@Override
	public void beforeCharacterOrHeading() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER_OR_HEADING);
	}

	@Override
	public void beforeFirstCharacter() {
		writeTypeSettingMark(MarkPosition.BEFORE_FIRST_CHARACTER_OR_HEADING);
	}

	@Override
	public void beforeCharacterHeading() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER_HEADING);
	}

	@Override
	public void afterCharacterHeading() {
		writeTypeSettingMark(MarkPosition.AFTER_CHARACTER_HEADING);
	}

	@Override
	public void beforeCharacter() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER);
	}

	@Override
	public void beforeStateDescription() {
		_printer.setIndent(0);
		writeTypeSettingMark(MarkPosition.BEFORE_STATE_DESCRIPTION);
	}

	@Override
	public void beforeCharacterNotes() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER_NOTES);
		_printer.printBufferLine();
	}

	@Override
	public void afterCharacterList() {
		writeTypeSettingMark(MarkPosition.AFTER_CHARACTER_LIST);
		writeTypeSettingMark(MarkPosition.END_OF_FILE);
		_printer.printBufferLine();
	}
	
	private void writeTypeSettingMark(String mark) {
		_printer.writeTypeSettingMark(mark);
	}
	
	private void writeTypeSettingMark(MarkPosition markPosition) {
		TypeSettingMark mark = _marks.get(markPosition.getId());
		if (mark != null) {
			writeTypeSettingMark(mark.getMarkText());
		}
	}
}
