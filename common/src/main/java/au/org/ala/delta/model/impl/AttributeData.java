package au.org.ala.delta.model.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.NumericRange;

public interface AttributeData {
	
	public String getValueAsString();
	
	public void setValueFromString(String value) throws DirectiveException;
	
	public boolean isStatePresent(int stateNumber);
	
	public boolean isSimple();

	public void setStatePresent(int stateNumber, boolean present);
	
	public boolean isUnknown();
	
    public boolean isVariable();
	
	public boolean isInapplicable();
	
	public boolean isExclusivelyInapplicable(boolean ignoreComments);
	
	public FloatRange getRealRange();
	public void setRealRange(FloatRange range);
	
	public Set<Integer> getPresentStateOrIntegerValues();
	
	public void setPresentStateOrIntegerValues(Set<Integer> values);
	
	public boolean hasValueSet();

	public boolean isRangeEncoded();

	boolean isCommentOnly();

	public List<NumericRange> getNumericValue();

	boolean isCodedUnknown();

	List<Integer> getPresentStatesAsList();
}
