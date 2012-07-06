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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.IdValidator;
import org.apache.commons.lang.math.IntRange;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * The IdValueList parses directive arguments in the form:
 * 
 * id1,value1 id2,value2 idn,valuen
 * where idx is a number or range of numbers.
 * 
 * This argument format is used by directives such as: CHARACTER WEIGHTS and
 * DECIMAL PLACES.
 * 
 */
public class IdValueListParser extends DirectiveArgsParser {

    private IdValidator _validator;

    public IdValueListParser(DeltaContext context, Reader reader, IdValidator validator) {
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
			readValueSeparator();
			BigDecimal value = readValue();
			
			for (int id : ids.toArray()) {
				_args.addDirectiveArgument(id, value);
			}
			
			skipWhitespace();
		}
	}
	
}
