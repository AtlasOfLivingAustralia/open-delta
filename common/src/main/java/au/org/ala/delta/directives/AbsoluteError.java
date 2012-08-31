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

/**
 * Processes the ABSOLUTE ERROR directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*ABSOLUTE_ERROR_
 */
public class AbsoluteError extends AbstractCharacterListDirective<DeltaContext, Double> {

	public AbsoluteError() {
		super("absolute", "error");
	}
	
	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {
		args.addNumericArgument(charIndex, value);
	}

	@Override
	protected Double interpretRHS(DeltaContext context, String rhs) {
		return Double.parseDouble(rhs);
	}

	@Override
	protected void processCharacter(DeltaContext context, int charIndex, Double error) {
		context.setAbsoluteError(charIndex, error);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARREALLIST;
	}
	
	@Override
    public int getOrder() {
    	return 4;
    }

}
