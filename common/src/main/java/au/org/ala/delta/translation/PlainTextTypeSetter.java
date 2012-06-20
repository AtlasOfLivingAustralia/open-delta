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

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Doesn't do much in the way of typesetting or formatting, inserts some blank lines 
 * for paragraph marks.
 */
public class PlainTextTypeSetter implements ItemListTypeSetter {

	private PrintFile _printer;
	private int _blankLinesBeforeItem;


    public PlainTextTypeSetter() {
        this(null, 0);
    }

	public PlainTextTypeSetter(PrintFile output) {
		this(output, 2);
	}
	
	public PlainTextTypeSetter(PrintFile output, int blankLinesBeforeItem) {
		_printer = output;
		_blankLinesBeforeItem = blankLinesBeforeItem;
		
	}

	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {
		_printer.writeBlankLines(_blankLinesBeforeItem, 5);
	}

	@Override
	public void afterItem(Item item) {}
	
	@Override
	public void beforeAttribute(Attribute attribute) {}
	
	@Override
	public void afterAttribute(Attribute attribute) {}
	
	@Override
	public void afterLastItem() {
		_printer.printBufferLine();
	}
	
	@Override
	public void beforeItemHeading() {}
	
	@Override
	public void afterItemHeading() {}
	
	@Override
	public void beforeItemName() {}

	@Override
	public void afterItemName() {}
	
	@Override
	public void newParagraph() {
		_printer.writeBlankLines(1, 2);
		_printer.setIndent(6);
		_printer.indent();
	}

	@Override
	public String typeSetItemDescription(String description) {
		return description;
	}

	@Override
	public void beforeNewParagraphCharacter() {	}

	@Override
	public String rangeSeparator() {
		return "-";
	}

	@Override
	public void beforeCharacterDescription(Character character, Item item) {}

	@Override
	public void afterCharacterDescription(Character character, Item item) {}

	@Override
	public void beforeEmphasizedCharacter() {}

	@Override
	public void afterEmphasizedCharacter() {}
	
}
