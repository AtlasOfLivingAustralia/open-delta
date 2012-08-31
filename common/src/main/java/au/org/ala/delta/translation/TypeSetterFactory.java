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
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.translation.print.FormattedItemNameTypeSetter;
import au.org.ala.delta.translation.print.FormattedUncodedCharactersTypeSetter;
import au.org.ala.delta.translation.print.UncodedCharactersTypeSetter;

/**
 * Creates the appropriate TypeSetter for the supplied context.  If the TYPESETTING MARKS
 * directive has been specified a FormattedTypeSetter will be returned. Otherwise a
 * PlainTextTypeSetter will be returned.
 * @param context the context in which the translator will run.
 * @param printer used for outputting to the print file.
 * @return a new instance of TypeSetter.
 */
public class TypeSetterFactory {
	
	public ItemListTypeSetter createTypeSetter(DeltaContext context, PrintFile printer) {
		
		if (context.isOmitTypeSettingMarks() || context.getTypeSettingMarks().isEmpty()) {
			return new PlainTextTypeSetter(printer);
		}
		else {
			return new FormattedTextTypeSetter(context,  printer);
		}
		
	}
	
	/**
	 * Used when creating typesetters for the PRINT ITEM NAMES and PRINT
	 * ITEM DESCRIPTIONS print actions.
	 */
	public ItemListTypeSetter createItemListTypeSetter(DeltaContext context, PrintFile printer) {
		return createItemListTypeSetter(context, printer, false);
	}
	
	
	/**
	 * Used when creating typesetters for the PRINT ITEM NAMES and PRINT
	 * ITEM DESCRIPTIONS print actions.
	 */
	public ItemListTypeSetter createItemListTypeSetter(DeltaContext context, PrintFile printer, boolean forPrint) {
		if (context.isOmitTypeSettingMarks() || context.getTypeSettingMarks().isEmpty()) {
			int blankLinesBeforeItem = 2;
			if (forPrint) {
				blankLinesBeforeItem = 1;
			}
			return new PlainTextTypeSetter(printer, blankLinesBeforeItem);
		}
		else {
			return new FormattedItemNameTypeSetter(context, printer);
		}
	}
	
	public ItemListTypeSetter createItemListTypeSetter(DeltaContext context, PrintFile printer, int blankLinesBeforeItem) {
		if (context.isOmitTypeSettingMarks() || context.getTypeSettingMarks().isEmpty()) {
			
			return new PlainTextTypeSetter(printer, blankLinesBeforeItem);
		}
		else {
			return new FormattedItemNameTypeSetter(context, printer);
		}
	}
	
	
	public CharacterListTypeSetter createCharacterListTypeSetter(DeltaContext context, PrintFile printer) {
		
		if (context.isOmitTypeSettingMarks() || context.getTypeSettingMarks().isEmpty()) {
			return new au.org.ala.delta.translation.print.PlainTextTypeSetter(printer);
		}
		else {
			return new au.org.ala.delta.translation.print.FormattedTypeSetter(context.getTypeSettingMarks(), printer);
		}
		
	}
	
	public UncodedCharactersTypeSetter createUncodedCharactersTypeSetter(DeltaContext context, PrintFile printer) {
		if (context.isOmitTypeSettingMarks() || context.getTypeSettingMarks().isEmpty()) {
			return new UncodedCharactersTypeSetter(printer);
		}
		else {
			return new FormattedUncodedCharactersTypeSetter( printer, context);
		}
	}
}
