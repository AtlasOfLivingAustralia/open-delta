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
 * Parses directives of the form:
 * #<integer>. <optional delimiter><text><optional delimiter>
 * #<integer>. <optional delimiter><text><optional delimiter>
 *
 * For example, this parser is used by the CHARACTER HEADINGS and ITEM SUB HEADINGS directives.
 */
public class IntegerTextListParser extends TextListParser<IntRange> {

    /** Validates the integers parsed by this class */
    private IntegerValidator _validator;

	public IntegerTextListParser(DeltaContext context, Reader reader, IntegerValidator validator) {
		super(context, reader);
        _validator = validator;
	}
	
	public IntegerTextListParser(DeltaContext context, Reader reader, boolean cleanWhitespace, IntegerValidator validator) {
		super(context, reader, cleanWhitespace);
        _validator = validator;
	}
	
	@Override
	protected void readSingle() throws ParseException {
		
		IntRange ids = readId();
		String comment = readOptionalComment();
		String value = readText();
		for (int id : ids.toArray()) {
			_args.addDirectiveArgument(id, comment, value);
		}
	}
	
	@Override
	protected IntRange readId() throws ParseException {
		expect(MARK_IDENTIFIER);
		
		readNext();
		IntRange ids = readIds(_validator);
		expect('.');
	    readNext();  // consume the . character.
	    return ids;
		
	}

}
