package au.org.ala.delta.directives.validation;

/**
 * Ensures an real is inside a specified range.
 */
public class RealRangeValidator implements RealValidator {
    private Double _min;
    private Double _max;

    private DirectiveError.Error _belowMinError;
    private DirectiveError.Error _aboveMaxError;

    /**
     * Creates a new RealRangeValidator that will enforce the supplied range.
     * @param min the minimum allowed value.  If null, a minimum value will not be enforced.
     * @param max the maximum allowed value. If null, a maximum value will not be enforced.
     */
    public RealRangeValidator(Double min, Double max) {
        this(min, max, DirectiveError.Error.VALUE_LESS_THAN_MIN, DirectiveError.Error.VALUE_GREATER_THAN_MAX);
    }

    /**
     * Creates a new IntegerRangeValidator that will enforce the supplied range.
     * @param min the minimum allowed value.  If null, a minimum value will not be enforced.
     * @param max the maximum allowed value. If null, a maximum value will not be enforced.
     * @param belowMinError the error to raise if the value is below the supplied min.
     * @param aboveMaxError the error to raise if the value is above the supplied max.
     */
    public RealRangeValidator(Double min, Double max, DirectiveError.Error belowMinError, DirectiveError.Error aboveMaxError) {
        _min = min;
        _max = max;
        _belowMinError = belowMinError;
        _aboveMaxError = aboveMaxError;
    }

    @Override
    public DirectiveError validateReal(double integer) {
        if (_min != null && integer < _min) {
            return new DirectiveError(_belowMinError, 0, _min);
        }
        if (_max != null && integer > _max) {
            return new DirectiveError(_aboveMaxError, 0, _max);
        }
        return null;
    }
}
