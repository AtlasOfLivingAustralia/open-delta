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
package au.org.ala.delta.translation.delta;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.VariantItem;
import au.org.ala.delta.translation.AbstractDataSetFilter;
import au.org.ala.delta.translation.DataSetFilter;

/**
 * The DataSetFilter is responsible for determining whether elements of a DeltaDataSet
 * should be included in a translation operation.
 */
public class DeltaFormatDataSetFilter extends AbstractDataSetFilter implements DataSetFilter {

	/**
	 * Creates a new DataSetFilter
	 * @param context
	 */
	public DeltaFormatDataSetFilter(DeltaContext context) {
		_context = context;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}
	
	
	@Override
	public boolean filter(Item item, Character character) {
		
		
		Attribute attribute = item.getAttribute(character);

		if (item.isVariant()) {
            if (item instanceof VariantItem) {
			    return outputVariantAttribute((VariantItem)item, character);
            }
		}
		
		if (attribute instanceof MultiStateAttribute && ((MultiStateAttribute)attribute).isImplicit()) {
			return outputImplictValue(attribute);
		}
		
		
		if (!item.hasAttribute(character)) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean filter(Character character) {
		try {
			return !_context.isCharacterExcluded(character.getCharacterId());
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
