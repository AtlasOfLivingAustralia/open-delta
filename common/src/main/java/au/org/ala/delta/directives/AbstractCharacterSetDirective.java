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

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdSetParser;

/**
 * An AbstractCharacterSetDirective is a directive that takes a space separated list of 
 * character sets of the form: c1:c2:...cn where cn is a character number or a range of numbers.
 */
public abstract class AbstractCharacterSetDirective<C extends AbstractDeltaContext> extends AbstractDirective<C> {

	protected DirectiveArguments args;
	
	public AbstractCharacterSetDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return args;
	}

	@Override
	public void parse(C context, String data) throws ParseException {
		IdSetParser parser = new IdSetParser(context, new StringReader(data));
		parser.parse();
		
		args = parser.getDirectiveArgs();
	}

	@Override
	public void process(C context, DirectiveArguments directiveArguments) throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			
			List<Integer> characters = arg.getDataList();
			
			processCharacterSet(context, characters);
		}
	}

	protected abstract void processCharacterSet(C context, List<Integer> characters);
}
