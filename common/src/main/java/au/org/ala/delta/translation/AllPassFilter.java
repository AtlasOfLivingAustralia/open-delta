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

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Lets all Items, Characters and Attributes through the filter.
 */
public class AllPassFilter implements DataSetFilter {

	@Override
	public boolean filter(Item item) {
		return true;
	}

	@Override
	public boolean filter(Item item, Character character) {
		return true;
	}

	@Override
	public boolean filter(Character character) {
		return true;
	}

}
