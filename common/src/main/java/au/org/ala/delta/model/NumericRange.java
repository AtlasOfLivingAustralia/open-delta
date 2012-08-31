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
package au.org.ala.delta.model;

import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.lang.math.Range;

public class NumericRange {

	private Number _extremeLow;
	private Number _extremeHigh;
	private Number _middle;
	private Range _range;
	
	private Double _absoluteError;
	private Double _percentageError;
	
	public NumericRange(Number extremeLow, Range range, Number middle, Number extremeHigh) {
		_extremeLow = extremeLow;
		_extremeHigh = extremeHigh;
		_middle = middle;
		_range = range;
		_absoluteError = null;
		_percentageError = null;
	}
	
	public NumericRange() {
		_extremeLow = null;
		_extremeHigh = null;
		_middle = null;
		_range = null;
		_percentageError = null;
		_absoluteError = null;
	}

	public Number getExtremeLow() {
		return _extremeLow;
	}

	public Number getExtremeHigh() {
		return _extremeHigh;
	}

	public Number getMiddle() {
		return _middle;
	}

	public Range getNormalRange() {
		if (_range.getMinimumNumber().equals(_range.getMaximumNumber())) {
			return createRangeForSingleValue(_range.getMinimumNumber());
		}
		
		return _range;
	}
	
	public Range getFullRange() {
		Number min = _range.getMinimumNumber();
		Number max = _range.getMaximumNumber();
		if (hasExtremeLow()) {
			min = _extremeLow;
		}
		
		if (hasExtremeHigh()) {
			max = _extremeHigh;
		}
		if (min.equals(max)) {
			return createRangeForSingleValue(min);
		}
		return new NumberRange(min, max);
	}

	public boolean hasExtremeLow() {
		return _extremeLow != null;
	}
	
	public boolean hasExtremeHigh() {
		return _extremeHigh != null;
	}
	
	public boolean hasMiddleValue() {
		return _middle != null;
	}

	public void setExtremeLow(Number extremeLow) {
		_extremeLow = extremeLow;
	}

	public void setExtremeHigh(Number extremeHigh) {
		_extremeHigh = extremeHigh;
	}

	public void setMiddle(Number middle) {
		_middle = middle;
	}

	public void setRange(Range range) {
		_range = range;
	}
	
	public void setAbsoluteError(double error) {
		if (_percentageError != null) {
			throw new IllegalArgumentException("Cannot specify both an absolute and percentage error!");
		}
		_absoluteError = error;
	}
	
	public void setPercentageError(double error) {
		if (_absoluteError != null) {
			throw new IllegalArgumentException("Cannot specify both an absolute and percentage error!");
		}
		_percentageError = error;
	}
	
	private Range createRangeForSingleValue(Number value) {
		
		if ((_absoluteError == null) && (_percentageError == null)) {
			return new NumberRange(value);
		}
		double val = value.doubleValue();
		if (_absoluteError != null) {
			return new NumberRange(val - _absoluteError, val + _absoluteError);
		}
		else {
			double lower = 100*val/(100+_percentageError);
			double upper = val*(100+_percentageError)/100;
			return new NumberRange(lower, upper);
		}	
	}
	
	/**
	 * @return the middle value if one exists, or the average of the normal
	 * range if it doesn't.
	 */
	public double middle() {
		if (hasMiddleValue()) {
			return _middle.doubleValue();
		}
		Range normal = getNormalRange();
		return (normal.getMinimumDouble() + normal.getMaximumDouble()) / 2;
		
	}
}
