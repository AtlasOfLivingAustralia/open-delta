package au.org.ala.delta.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.impl.AttributeData;

public class MultiStateAttribute extends Attribute {

    public MultiStateAttribute(MultiStateCharacter character, AttributeData impl) {
        super(character, impl);
        // TODO Auto-generated constructor stub
    }
    
    public boolean isStatePresent(int stateNumber) {
        
        if (!(_character instanceof MultiStateCharacter)) {
            return false;
        }
        
        
        boolean statePresent = _impl.isStatePresent(stateNumber);
        
        if ((statePresent == false) && (StringUtils.isEmpty(getValueAsString()))) {
            MultiStateCharacter multiStateChar = (MultiStateCharacter)_character;
            statePresent = (stateNumber == multiStateChar.getUncodedImplicitState());
        }
        return statePresent;
    }
    
    public void setStatePresent(int stateNumber, boolean present) {
        if (!(_character instanceof MultiStateCharacter)) {
            return;
        }
        
        _impl.setStatePresent(stateNumber, present);
        
        notifyObservers();
    }
    
    public List<Integer> getPresentStates() {
    	return _impl.getPresentStates();
    }

    /**
     * @return true if this attribute has been coded as "V" (variable).
     */
	public boolean isVariable() {
		return _impl.isVariable();
	}

}
