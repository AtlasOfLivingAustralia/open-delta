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
package au.org.ala.delta.dist.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.IntegerValidator;
import au.org.ala.delta.directives.validation.PositiveIntegerValidator;
import au.org.ala.delta.dist.DistContext;

/**
 * Processes the MINIMUM NUMBER OF COMPARISONS directive.
 *
 */
public class MinimumNumberOfComparisons extends AbstractIntegerDirective {
	
	public MinimumNumberOfComparisons() {
		super("minimum", "number", "of", "comparisons");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTEGER;
	}
	
	@Override
	protected void processInteger(DeltaContext context, int value) throws Exception {
		((DistContext)context).setMinimumNumberOfComparisons(value);
	
	}

    @Override
    protected IntegerValidator createValidator(DeltaContext context) {
        return new PositiveIntegerValidator();
    }
}
