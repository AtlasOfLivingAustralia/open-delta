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
