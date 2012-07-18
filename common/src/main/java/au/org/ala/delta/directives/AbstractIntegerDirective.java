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
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ParsingUtils;
import au.org.ala.delta.directives.validation.IntegerValidator;

import java.text.ParseException;

public abstract class AbstractIntegerDirective extends AbstractDirective<DeltaContext> {

	private int _value = -1;

	protected AbstractIntegerDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {

		DirectiveArguments args = new DirectiveArguments();
		args.addDirectiveArgument(_value);
		return args;
	}

	
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_value = ParsingUtils.readInt(context.getCurrentParsingContext(), createValidator(context), data.trim());
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		processInteger(context, _value);
		
	}

	protected abstract void processInteger(DeltaContext context, int character) throws Exception;

    /**
     * Subclasses should override this method to create a validator appropriate for the directive type.
     * @param context the current parsing/processing context.
     * @return either an appropriate instance of IntegerValidator or null if validation is not required.
     */
    protected abstract IntegerValidator createValidator(DeltaContext context);

}
