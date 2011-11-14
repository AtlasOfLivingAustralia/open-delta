/*******************************************************************************
Abstr * Copyright (C) 2011 Atlas of Living Australia
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
package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.model.CharacterType;

/**
 * Allows the value of an Item attribute to be used instead of the Item description.
 */
public class CharacterForTaxonNames extends AbstractIntegerDirective {
	
	public CharacterForTaxonNames() {
		super("character", "for", "taxon", "names");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHAR;
	}
	
	@Override
	public void processInteger(DeltaContext context, int character) throws Exception {
		
		if (context.getDataSet().getCharacter(character).getCharacterType() != CharacterType.Text) {
			throw new RuntimeException("149,1");
		}
		context.setCharacterForTaxonNames(character);
	}

	
	@Override
	public int getOrder() {
		return 4;
	}
	
}
