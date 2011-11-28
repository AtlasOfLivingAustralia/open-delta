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

import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ParsingUtils;

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
		_value = ParsingUtils.readInt(context.getCurrentParsingContext(), data.trim());
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		processInteger(context, _value);
		
	}

	protected abstract void processInteger(DeltaContext context, int character) throws Exception;

}
