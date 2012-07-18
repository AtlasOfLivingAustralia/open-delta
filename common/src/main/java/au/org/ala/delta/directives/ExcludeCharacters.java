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
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.directives.validation.IntegerValidator;

/**
 * Implements the EXCLUDE CHARACTERS directive.  Accepts a list of character numbers which are added to the
 * Set of excluded characters maintained by the DeltaContext.
 */
public class ExcludeCharacters extends AbstractRangeListDirective<DeltaContext> {
	
	public static final String[] CONTROL_WORDS = {"exclude", "characters"};
	
	public ExcludeCharacters() {
		super(CONTROL_WORDS);
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.includeAllCharacters();
		super.process(context, directiveArguments);
	}

	@Override
	protected void processNumber(DeltaContext context, int number) throws DirectiveException {
		context.excludeCharacter(number);
	}

	@Override
	public int getOrder() {
		return 4;
	}

    @Override
    protected IntegerValidator createValidator(DeltaContext context) {
        return new CharacterNumberValidator(context);
    }
}
