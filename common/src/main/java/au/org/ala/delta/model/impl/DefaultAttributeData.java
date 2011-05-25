package au.org.ala.delta.model.impl;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;


/**
 * A simple implementation of AttributeData that stores attribute data
 * in-memory.
 */
public class DefaultAttributeData implements AttributeData {

    private String _value;

    @Override
    public String getValueAsString() {
        return _value;
    }

    @Override
    public void setValueFromString(String value) {
        _value = value;
    }

    @Override
    public boolean isStatePresent(int stateNumber) {
        return false;
    }

    public void setStatePresent(int stateNumber, boolean present) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isUnknown() {
        String value = getValueAsString();
        return ("U".equals(value) || (StringUtils.isEmpty(value)));
    }

    @Override
    public boolean isInapplicable() {
        return "-".equals(_value);
    }


    @Override
    public FloatRange getRealRange() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRealRange(FloatRange range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> getPresentStateOrIntegerValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPresentStateOrIntegerValues(Set<Integer> values) {
        throw new UnsupportedOperationException();
    }

	@Override
	public boolean isVariable() {
		return "V".equals(_value);
	}

    @Override
    public boolean hasValueSet() {
        return !StringUtils.isEmpty(_value);
    }
	
	
}
