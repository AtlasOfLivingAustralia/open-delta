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
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.AbstractDataSetFilter;
import au.org.ala.delta.translation.DataSetFilter;

/**
 * The DataSetFilter is responsible for determining whether elements of a DeltaDataSet
 * should be included in a translation operation.
 */
public class UncodedCharactersFilter extends AbstractDataSetFilter implements DataSetFilter {

	private boolean _filterChars = false;
	
	/**
	 * Creates a new DataSetFilter
	 * @param context
	 */
	public UncodedCharactersFilter(DeltaContext context, boolean filterChars) {
		_context = context;
		_filterChars = filterChars;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}
	
	
	@Override
	public boolean filter(Item item, Character character) {
		
		return filter(character);
	}
	
	@Override
	public boolean filter(Character character) {
		if (!_filterChars) {
			return true;
		}
		return !_context.isCharacterExcluded(character.getCharacterId());
	}
}
