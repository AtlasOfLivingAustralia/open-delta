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
import au.org.ala.delta.directives.OutputParameters;
import au.org.ala.delta.model.*;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.ItemListTypeSetterAdapter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;

/**
 * Writes the item descriptions to the print file.
 */
public class ItemDescriptionsPrinter extends DeltaFormatTranslator {
	
	private ItemListTypeSetter _typeSetter;
	private AttributeFormatter _attributeFormatter;
	
	public ItemDescriptionsPrinter(
			DeltaContext context,
			PrintFile printer,
			ItemFormatter itemFormatter, 
			AttributeFormatter attributeFormatter,
			ItemListTypeSetter typeSetter) {
		super(context, printer, itemFormatter, null, attributeFormatter, null, new ItemListTypeSetterAdapter());
		_typeSetter = typeSetter;
		_attributeFormatter = attributeFormatter;
	}
	
	@Override
	public void beforeFirstItem() {}
	
	@Override
	public void beforeItem(Item item) {
		_typeSetter.beforeItem(item);
		super.beforeItem(item);
		_typeSetter.afterItemName();
	}

	@Override
	public void afterItem(Item item) {
		super.afterItem(item);
		
	}
	
	@Override
	public void afterLastItem() {
		_typeSetter.afterLastItem();
	}
	
	@Override
	protected String getAttributeValue(Attribute attribute) {
		String value = super.getAttributeValue(attribute);
		return _attributeFormatter.formatComment(value);
	}

    @Override
    public void beforeFirstCharacter() {}

    @Override
    public void beforeCharacter(au.org.ala.delta.model.Character character) {}

    @Override
    public void afterCharacter(Character character) {}

    @Override
    public void translateOutputParameter(OutputParameters.OutputParameter parameter) {}


}
