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

import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveError.Error;
import au.org.ala.delta.directives.validation.RealValidator;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.directives.validation.IntegerValidator;

/**
 * Utility class for associating common parsing operations (e.g. string to 
 * numeric conversions) and raising the appropriate errors if it fails.
 */
public class ParsingUtils {
	
	public static int readInt(ParsingContext context, IntegerValidator validator, String value) throws DirectiveException {
		try {
            int currentPos = (int)context.getCurrentOffset();
			int intValue = Integer.parseInt(value);
            if (validator != null) {
                DirectiveError error = validator.validateInteger(intValue);
                if (error != null) {
                    error.setPosition(currentPos);
                    throw error.asException();
                }
            }
            return intValue;
		}
        catch (NumberFormatException e) {
			throw DirectiveError.asException(Error.INTEGER_EXPECTED, (int)context.getCurrentOffset());
		}

	}
	
	   public static double readReal(ParsingContext context, RealValidator validator, String value) throws DirectiveException {
	        try {
	            int currentPos = (int)context.getCurrentOffset();
	            double realValue = Double.parseDouble(value);
	            if (validator != null) {
	                DirectiveError error = validator.validateReal(realValue);
	                if (error != null) {
	                    error.setPosition(currentPos);
	                    throw error.asException();
	                }
	            }
	            return realValue;
	        }
	        catch (NumberFormatException e) {
	            throw DirectiveError.asException(Error.INTEGER_EXPECTED, (int)context.getCurrentOffset());
	        }

	    }
}
