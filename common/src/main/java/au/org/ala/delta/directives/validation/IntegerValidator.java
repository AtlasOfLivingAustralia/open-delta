package au.org.ala.delta.directives.validation;

/**
 * An IntegerValidator is responsible for validation of an id (Character number, Item number or state number) during
 * directive parsing.  It is designed to be invoked during the parsing so that if an error occurs the current
 * parse position is known and hence the error position can be highlighted to the user.
 */
public interface IntegerValidator {

    /**
     * Validates the supplied id, and returns a specific instance of DirectiveError if the id is found to be
     * invalid.  Implementations should return null if the id is valid.
     * @param id the id (Character, Item or State number) to validate.
     * @return either null (if the id is valid) or an instance of DirectiveError that contains the reason the id
     * was deemed to be invalid.
     */
    public DirectiveError validateInteger(int id);
}
