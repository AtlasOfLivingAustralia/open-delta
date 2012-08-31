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
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Filters exclusively on whether the Item or Character has been excluded
 * via include/exclude/Item/Character calls on the DistContext.
 *
 */
public class IncludeExcludeDataSetFilter implements DataSetFilter {

	private DeltaContext _context;
	
	public IncludeExcludeDataSetFilter(DeltaContext context) {
		_context = context;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}
	

	@Override
	public boolean filter(Item item, Character character) {
		return true;
	}

	@Override
	public boolean filter(Character character) {
		return !_context.isCharacterExcluded(character.getCharacterId());
	}

}
