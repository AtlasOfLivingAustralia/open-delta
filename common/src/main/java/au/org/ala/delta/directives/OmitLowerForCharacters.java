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
package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.directives.validation.IdValidator;


/**
 * Processes the OMIT LOWER FOR CHARACTERS directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*OMIT_LOWER_FOR
 */
public class OmitLowerForCharacters extends AbstractRangeListDirective<DeltaContext> {

	
	public OmitLowerForCharacters() {
		super("omit", "lower", "for", "characters");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.setOmitLowerForCharacter(number, true);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

    @Override
    protected IdValidator createValidator(DeltaContext context) {
        return new CharacterNumberValidator(context);
    }
}
