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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;


public abstract class AbstractCustomDirective extends AbstractDirective<DeltaContext> {

	protected DirectiveArguments _args;
	
	protected AbstractCustomDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		
		StringReader reader = new StringReader(data);
		DirectiveArgsParser parser = createParser(context, reader);
		
		try {
			parser.parse();
		}
		catch(Exception e) {
			if (e instanceof ParseException) {
				throw (ParseException)e;
			}
			else {
				throw new RuntimeException(e);
			}
		}
		
		_args = parser.getDirectiveArgs();
	}
	
	protected abstract DirectiveArgsParser createParser(DeltaContext context, StringReader reader);
}
