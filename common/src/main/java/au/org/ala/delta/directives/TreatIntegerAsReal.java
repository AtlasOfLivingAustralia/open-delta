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
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;
import au.org.ala.delta.directives.validation.UniqueIntegerValidator;

import java.io.StringReader;
import java.text.ParseException;

/**
 * Processes the TREAT INTEGER AS REAL directive.
 */
public class TreatIntegerAsReal extends AbstractDirective<DeltaContext> {

	public TreatIntegerAsReal() {
		super("treat", "integer", "as", "real");
	}
	
	private DirectiveArguments _args;
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		IdListParser parser = new IdListParser(context, new StringReader(data), new UniqueIntegerValidator());
		parser.parse();
		_args = parser.getDirectiveArgs();
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			context.setTreatIntegerCharacterAsReal((Integer)arg.getId(), true);
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
