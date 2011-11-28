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

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.UniqueIdValidator;

/**
 * The IdValueList parses directive arguments in the form:
 * 
 * id1 id2 idn
 * where idx is a number or range of numbers.
 * 
 * This argument format is used by directives such as: EXCLUDE ITEMS and
 * INCLUDE CHARACTERS.
 * 
 */
public class IdListParser extends DirectiveArgsParser {
	
	private UniqueIdValidator _validator;
	
	public IdListParser(DeltaContext context, Reader reader) {
		super(context, reader);
		
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			IntRange ids = readIds();
			
			//validate(ids);
			
			for (int id : ids.toArray()) {
				_args.addDirectiveArgument(id);
			}
			
			skipWhitespace();
		}
	}
	
	
	private void validate(IntRange ids) {
		DirectiveError error = _validator.validateIds(ids, _position);
		if (error != null) {
			_context.addError(error);
		}
	}
}
