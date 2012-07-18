package au.org.ala.delta.directives.validation;

/**
 * Ensures that the supplied number is greater than or equal to zero.
 */
public class PositiveIntegerValidator extends IntegerRangeValidator {

    public PositiveIntegerValidator() {
        super(0, null);
    }

    public PositiveIntegerValidator(DirectiveError.Error negativeNumberError) {
        super(0, null, negativeNumberError, null);
    }
}
