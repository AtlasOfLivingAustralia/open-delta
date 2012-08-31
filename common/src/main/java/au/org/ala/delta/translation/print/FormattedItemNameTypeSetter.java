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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.translation.FormattedTextTypeSetter;
import au.org.ala.delta.translation.PrintFile;

public class FormattedItemNameTypeSetter extends FormattedTextTypeSetter {

	public FormattedItemNameTypeSetter(DeltaContext context, PrintFile typeSetter) {
		super(context, typeSetter);
	}

	@Override 
	public void beforeItem(Item item) {
		writeTypeSettingMark(MarkPosition.BEFORE_ITEM_WITH_NATURAL_LANGUAGE);
	}
	
	@Override
	public void beforeItemName() {
		writeTypeSettingMark(MarkPosition.ITEM_DESCRIPTION_BEFORE_ITEM_NAME);
	}

	@Override
	public void afterItemName() {
		writeTypeSettingMark(MarkPosition.ITEM_DESCRIPTION_AFTER_ITEM_NAME);
	}
}
