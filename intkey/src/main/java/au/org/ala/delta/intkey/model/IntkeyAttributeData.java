package au.org.ala.delta.intkey.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.impl.AttributeData;

public class IntkeyAttributeData implements AttributeData {

    private String _textValue;
    private Set<Integer> _stateOrIntegerValues;
    private FloatRange _realRange;
    
    private boolean _unknown;
    private boolean _inapplicable;

    public IntkeyAttributeData(boolean unknown, boolean inapplicable) {
        _stateOrIntegerValues = new HashSet<Integer>();
        _unknown = unknown;
        _inapplicable = inapplicable;
        
    }

    @Override
    public String getValueAsString() {
        return _textValue;
    }

    @Override
    public void setValueFromString(String value) {
        _textValue = value;
    }

    @Override
    public boolean isStatePresent(int stateNumber) {
        return _stateOrIntegerValues.contains(stateNumber);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public void setStatePresent(int stateNumber, boolean present) {
        if (present) {
            _stateOrIntegerValues.add(stateNumber);
        } else {
            _stateOrIntegerValues.remove(stateNumber);
        }
    }

    @Override
    public boolean isUnknown() {
        return _unknown;
    }
    
    @Override
    public boolean isCodedUnknown() {
        return _unknown;
    }

    @Override
    public boolean isInapplicable() {
        return _inapplicable;
    }
    
    @Override
    public boolean isExclusivelyInapplicable(boolean ignoreComment) {
        return _inapplicable;
    }

    @Override
    public FloatRange getRealRange() {
        return _realRange;
    }

    @Override
    public void setRealRange(FloatRange range) {
        _realRange = range;
    }

    @Override
    public Set<Integer> getPresentStateOrIntegerValues() {
     // no defensive copy here for efficiency when creating attributes after reading file
        return _stateOrIntegerValues;
    }

    @Override
    public void setPresentStateOrIntegerValues(Set<Integer> values) {
        // no defensive copy here for efficiency when creating attributes after reading file
        _stateOrIntegerValues = values;
    }

    @Override
    public boolean isVariable() {
        // TODO does this have any relevance in intkey?
        return false;
    }

    @Override
    public boolean hasValueSet() {
        return StringUtils.isEmpty(_textValue) && _realRange == null && _stateOrIntegerValues.isEmpty();
    }
    
    @Override
    public boolean isRangeEncoded() {
    	return false;
    }

    @Override
    public boolean isCommentOnly() {
    	return ((_stateOrIntegerValues.size() == 0) && (_realRange == null));
    }

	@Override
	public List<NumericRange> getNumericValue() {
		throw new UnsupportedOperationException();
	}
    
    
}
