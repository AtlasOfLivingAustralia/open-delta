package au.org.ala.delta.model;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.model.impl.AttributeData;

public class MultiStateAttribute extends Attribute {
    
    public MultiStateAttribute(MultiStateCharacter character, AttributeData impl) {
        super(character, impl);
    }
    
    @Override
    public MultiStateCharacter getCharacter() {
        return (MultiStateCharacter) super.getCharacter();
    }

    public boolean isStatePresent(int stateNumber) {
        
        boolean statePresent = _impl.isStatePresent(stateNumber);
        
        if (statePresent == false && isImplicit()) {
            statePresent = (stateNumber == getImplicitValue());
        }
        return statePresent;
    }
    
    /**
     * Only returns true if the supplied state has been coded.
     * This is in contrast to isStatePresent which will return true if the
     * state number is the implicit value and this attribute is uncoded.
     * @param stateNumber the state number to check.
     */
    public boolean isStateCoded(int stateNumber) {
    	return _impl.isStatePresent(stateNumber);
    }
    
    public void setStatePresent(int stateNumber, boolean present) {
        _impl.setStatePresent(stateNumber, present);
        
        notifyObservers();
    }
    
    public Set<Integer> getPresentStates() {
        Set<Integer> presentStates;
        
        if (!_impl.hasValueSet() && isImplicit()) {
            presentStates = new HashSet<Integer>();
            presentStates.add(getImplicitValue());
        } else {
            presentStates = _impl.getPresentStateOrIntegerValues();
        }
        
        return presentStates;
    }
    
    public void setPresentStates(Set<Integer> states) {
        _impl.setPresentStateOrIntegerValues(states);
        notifyObservers();
    }
    
    /**
     * An implicit value is one for which no attribute value is coded but an implicit value
     * has been specified for the attributes character.
     * @return true if the value of this attribute is derived from the Characters implicit value.
     */
    public boolean isImplicit() {
        return (!_impl.hasValueSet() && getCharacter().getUncodedImplicitState() > 0);
    }
    
    /**
     * @return the implicit value of this attribute.
     */
    public int getImplicitValue() {
        if (!isImplicit()) {
            throw new IllegalStateException("Cannot get an implict value on an attribute that is not implicit.");
        }
        return getCharacter().getUncodedImplicitState();
    }

    /**
     * @return true if this attribute has been coded as "V" (variable).
     */
	public boolean isVariable() {
		return _impl.isVariable();
	}
	
	/**
	 * @return true if this encoding for this attribute includes a range of states.
	 */
	public boolean isRangeEncoded() {
		return _impl.isRangeEncoded();
	}

    @Override
    public boolean isUnknown() {
        return _impl.isUnknown() && !isImplicit();
    }
	

}
