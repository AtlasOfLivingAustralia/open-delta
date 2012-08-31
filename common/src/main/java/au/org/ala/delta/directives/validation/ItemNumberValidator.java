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
 * Validates that the parsed id maps to a valid item in the data set as well as supplied item numbers
 * are unique.
 * Note that the validation is based on the supplied MAXIMUM NUMBER OF ITEMS - the actual number of items is
 * typically not known until the ITEM DESCRIPTIONS directive is parsed (which is order 5 and hence comes
 * last in a directives file).
 */
public class ItemNumberValidator extends UniqueIntegerValidator {

    private DeltaContext _context;

    public ItemNumberValidator(DeltaContext context) {
        _context = context;
    }

    @Override
    public DirectiveError validateInteger(int id) {
        if (id < 1) {
            return new DirectiveError(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, DirectiveError.UNKNOWN_POSITION);
        }
        else if (id > _context.getMaximumNumberOfItems()) {
            return new DirectiveError(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, DirectiveError.UNKNOWN_POSITION, _context.getMaximumNumberOfItems());
        }

        return super.validateInteger(id);
    }


}
