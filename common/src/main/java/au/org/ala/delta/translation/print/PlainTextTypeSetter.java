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

import au.org.ala.delta.translation.PrintFile;

public class PlainTextTypeSetter implements CharacterListTypeSetter {

	protected PrintFile _printer;
	
	public PlainTextTypeSetter(PrintFile printer) {
		_printer = printer;
	}

	@Override
	public void beforeCharacterOrHeading() {
		
	}

	@Override
	public void beforeFirstCharacter() {
		_printer.setLineWrapIndent(10);
	}

	@Override
	public void beforeCharacterHeading() {
		_printer.writeBlankLines(1, 0);
	}

	@Override
	public void afterCharacterHeading() {
		
	}

	@Override
	public void beforeCharacter() {
		
	}

	@Override
	public void beforeStateDescription() {
		_printer.setIndent(7);
	}

	@Override
	public void beforeCharacterNotes() {
		
	}

	@Override
	public void afterCharacterList() {
		
	}

}
