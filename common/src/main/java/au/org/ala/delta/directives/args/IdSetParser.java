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
package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.directives.AbstractDeltaContext;

/**
 * The IdSetParser parses sets of linked values, for example
 * the LINKED CHARACTERS directive.
 */
public class IdSetParser extends DirectiveArgsParser {
	
	public IdSetParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			addSet();
			
			skipWhitespace();
		}
		
	}
	
	private void addSet() throws ParseException {
		List<Integer> values = readSet();
		
		if (values.size() > 0) {
			_args.addDirectiveArgument(values);
		}
	}
}
