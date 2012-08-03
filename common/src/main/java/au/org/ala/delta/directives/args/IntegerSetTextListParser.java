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

import java.io.Reader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;


/**
 * Parses directives of the form:
 * #<integer set>. <optional delimiter><text><optional delimiter>
 * #<integer set>. <optional delimiter><text><optional delimiter>
 *
 * For example, this parser is used by the CHARACTER NOTES directive.
 */
public class IntegerSetTextListParser extends TextListParser<Set<Integer>> {

    /** Validates the integers parsed by this class */
    private IntegerValidator _validator;

    private static final char SET_TERMINATOR = '.';

	public IntegerSetTextListParser(DeltaContext context, Reader reader, IntegerValidator validator) {
		super(context, reader);
        _validator = validator;
	}
	
	@Override
	protected void readSingle() throws ParseException {
		
		Set<Integer> ids = readId();
		String comment = readOptionalComment();
		String value = readText();
		for (int id : ids) {
			_args.addDirectiveArgument(id, comment, value);
		}
	}
	
	@Override
	protected Set<Integer> readId() throws ParseException {
		expect(MARK_IDENTIFIER);
		
		readNext();
        Set<Integer> ids = new HashSet<Integer>(readSet(_validator, SET_TERMINATOR));
		expect(SET_TERMINATOR);
	    readNext();  // consume the . character.
	    return ids;
		
	}

}
