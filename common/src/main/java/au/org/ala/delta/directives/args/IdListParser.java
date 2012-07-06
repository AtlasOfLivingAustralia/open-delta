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

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.validation.IdValidator;
import org.apache.commons.lang.math.IntRange;

import java.io.Reader;
import java.text.ParseException;

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
	
	private IdValidator _validator;
	
	public IdListParser(AbstractDeltaContext context, Reader reader, IdValidator validator) {
		super(context, reader);
        _validator = validator;
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			IntRange ids = readIds(_validator);

			for (int id : ids.toArray()) {
				_args.addDirectiveArgument(id);
			}
			
			skipWhitespace();
		}
	}

}
