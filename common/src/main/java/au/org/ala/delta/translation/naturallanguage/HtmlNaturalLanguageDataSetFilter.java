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
package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * The DataSetFilter is responsible for determining whether elements of a DeltaDataSet
 * should be included in a translation operation.
 * The HtmlNaturalLanguageDataSetFilter differs from it's parent in that
 * it allows the Character specified by the Character for Taxon Images
 * directive through the filter.
 */
public class HtmlNaturalLanguageDataSetFilter extends NaturalLanguageDataSetFilter {

	private int _charForTaxonImages;
	
	/**
	 * Creates a new DataSetFilter
	 * @param context
	 */
	public HtmlNaturalLanguageDataSetFilter(DeltaContext context) {
		super(context);
		Integer charForTaxonImages = _context.getCharacterForTaxonImages();
		if (charForTaxonImages != null) {
			_charForTaxonImages = charForTaxonImages;
		}
		else {
			_charForTaxonImages = -1;
		}
	}
	
	@Override
	public boolean filter(Item item, Character character) {
		
		boolean result = super.filter(item, character);
		
		return result || character.getCharacterId() == _charForTaxonImages ;
	}
}
