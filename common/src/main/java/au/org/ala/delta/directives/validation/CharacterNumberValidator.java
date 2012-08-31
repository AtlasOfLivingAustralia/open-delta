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
package au.org.ala.delta.directives.validation;

import au.org.ala.delta.DeltaContext;

/**
 * Validates that the parsed id maps to a valid character in the data set as well as supplied character numbers
 * are unique.
 */
public class CharacterNumberValidator implements IntegerValidator {

    private DeltaContext _context;

    public CharacterNumberValidator(DeltaContext context) {
        _context = context;
    }

    @Override
    public DirectiveError validateInteger(int id) {
        if (id < 1) {
            return new DirectiveError(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, DirectiveError.UNKNOWN_POSITION);
        }
        else if (id > _context.getNumberOfCharacters()) {
            return new DirectiveError(DirectiveError.Error.CHARACTER_NUMBER_TOO_HIGH, DirectiveError.UNKNOWN_POSITION, _context.getNumberOfCharacters());
        }

        return null;
    }


}
