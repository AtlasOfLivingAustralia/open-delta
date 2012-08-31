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
package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.CharacterType;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Parses the DECIMAL PLACES directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*DECIMAL_PLACES_
 */
public class DecimalPlaces extends AbstractCharacterListDirective<DeltaContext, Integer> {

    public static final String[] CONTROL_WORDS =  {"decimal", "places"};

    public DecimalPlaces() {
        super(CONTROL_WORDS);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_CHARINTEGERLIST;
    }

    @Override
    protected void addArgument(DirectiveArguments args, int charIndex, String value) throws DirectiveException {
        try {
            args.addDirectiveArgument(charIndex, new BigDecimal(Integer.parseInt(value)));
        }
        catch (NumberFormatException e) {
            throw DirectiveError.asException(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, 0);
        }
    }

    @Override
    protected Integer interpretRHS(DeltaContext context, String rhs) throws DirectiveException {
        try {
            return Integer.parseInt(rhs);
        }
        catch (NumberFormatException e) {
            throw DirectiveError.asException(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, 0);
        }
     }

    @Override
    protected void processCharacter(DeltaContext context, int charNumber, Integer decimalPlaces) throws ParseException {
        if (context.getCharacter(charNumber).getCharacterType() == CharacterType.RealNumeric) {
            context.addDecimalPlace(charNumber, decimalPlaces);
        }
        else {
            context.addError(new DirectiveError(DirectiveError.Warning.CHARACTER_IS_NOT_REAL, 0, charNumber));
        }
    }
}
