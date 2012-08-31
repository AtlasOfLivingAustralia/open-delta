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
import au.org.ala.delta.directives.validation.IntegerValidator;
import org.apache.commons.lang.math.IntRange;

import java.io.Reader;
import java.text.ParseException;

/**
 * The IdWithIdListParser parses directive arguments in the form:
 * 
 * #firstid1 id1 id2 idn
 * #firstid2 id1 id2 idn
 * 
 * The firstid can be numeric or an item description.
 * 
 * This argument format is used by directives such as: EMPHASIZE CHARACTERS and
 * ADD CHARACTERS.
 * 
 */
public class IdWithIdListParser extends DirectiveArgsParser {

    protected IntegerValidator _validator;
    private IntegerValidator _listValidator;

    public IdWithIdListParser(DeltaContext context, Reader reader, IntegerValidator integerValidator, IntegerValidator integerListValidator) {
		super(context, reader);
        _validator = integerValidator;
        _listValidator = integerListValidator;
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			expect('#');
			
			readIdAndIdList();
		}
	}
	
	
	private void readIdAndIdList() throws ParseException {
		
		Object id = readId();
		skipWhitespace();
		
		DirectiveArgument<?> arg;
		if (id instanceof String) {
			arg = new DirectiveArgument<String>((String)id);
		}
		else {
			arg = new DirectiveArgument<Integer>((Integer)id);
		}
		
		while (_currentInt >=0 && _currentChar != '#') {
			
			IntRange ids = readIds(_listValidator);
			for (int tmpId : ids.toArray()) {
				arg.add(tmpId);
			}
			
			skipWhitespace();
		}
		
		_args.add(arg);
		
	}
	
	protected Object readId() throws ParseException {
		expect('#');
		
		mark();
		readNext();
		if (Character.isDigit(_currentChar)) {
			reset();
			
			return readListId(_validator);
		}
		else {
			reset();
			
			return readItemDescription();
		}
	}
}
