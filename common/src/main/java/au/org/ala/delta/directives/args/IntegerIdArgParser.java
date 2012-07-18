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
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.directives.validation.IntegerValidator;

import java.io.Reader;
import java.text.ParseException;

/**
 * The IntegerIdArgParser parses directive arguments in the form:
 * 
 * id
 * where id is a number.
 * 
 * This argument format is used by directives such as: 
 * CHARACTER FOR TAXON NAMES and STOP AFTER ITEM.
 * 
 */
public class IntegerIdArgParser extends DirectiveArgsParser {

    private IntegerValidator _validator;

	public IntegerIdArgParser(DeltaContext context, Reader reader, IntegerValidator validator) {
		super(context, reader);

        _validator = validator;
	}
	
	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		skipWhitespace();
		try {
            int currentPos = (int)_context.getCurrentParsingContext().getCurrentOffset();
			Integer id = Integer.parseInt(readFully().trim());
            validate(currentPos, id);
            _args.addDirectiveArgument(id);
		}
		catch (NumberFormatException e) {
			throw DirectiveError.asException(DirectiveError.Error.INTEGER_EXPECTED, 0);
		}
	}

    private void validate(int currentPos, Integer id) throws DirectiveException {
        if (_validator != null) {
            DirectiveError error = _validator.validateInteger(id);
            if (error != null) {
                error.setPosition(currentPos);
                throw error.asException();
            }
        }
    }
}
