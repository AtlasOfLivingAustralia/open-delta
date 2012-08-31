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

/**
 * An IntegerValidator is responsible for validation of a real number during
 * directive parsing.  It is designed to be invoked during the parsing so that if an error occurs the current
 * parse position is known and hence the error position can be highlighted to the user.
 */
public interface RealValidator {
    /**
     * Validates the supplied real value, and returns a specific instance of DirectiveError if the id is found to be
     * invalid.  Implementations should return null if the id is valid.
     * @param value the real value validate.
     * @return either null (if the value is valid) or an instance of DirectiveError that contains the reason the value
     * was deemed to be invalid.
     */
    public DirectiveError validateReal(double validate);
}
