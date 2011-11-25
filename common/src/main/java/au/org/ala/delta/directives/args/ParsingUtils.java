package au.org.ala.delta.directives.args;

import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveError.Error;

import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * Utility class for associating common parsing operations (e.g. string to 
 * numeric conversions) and raising the appropriate errors if it fails.
 */
public class ParsingUtils {
	
	public static int readInt(ParsingContext context, String value) throws DirectiveException {
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			throw DirectiveError.asException(Error.INTEGER_EXPECTED, (int)context.getCurrentOffset());
		}
	}
}
