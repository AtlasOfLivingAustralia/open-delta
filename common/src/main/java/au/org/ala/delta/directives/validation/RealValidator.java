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
