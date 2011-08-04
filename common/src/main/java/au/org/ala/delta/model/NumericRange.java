package au.org.ala.delta.model;

import org.apache.commons.lang.math.Range;

public class NumericRange {

	private Number _extremeLow;
	private Number _extremeHigh;
	private Number _middle;
	private Range _range;
	
	public NumericRange(Number extremeLow, Range range, Number middle, Number extremeHigh) {
		_extremeLow = extremeLow;
		_extremeHigh = extremeHigh;
		_middle = middle;
		_range = range;
	}
	
	public NumericRange() {
		_extremeLow = null;
		_extremeHigh = null;
		_middle = null;
		_range = null;
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

	public Range getRange() {
		return _range;
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
	
	
}
