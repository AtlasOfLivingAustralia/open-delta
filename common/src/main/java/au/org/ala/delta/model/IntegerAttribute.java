package au.org.ala.delta.model;

import java.util.Set;

import au.org.ala.delta.model.impl.AttributeData;

public class IntegerAttribute extends NumericAttribute {

    
    public IntegerAttribute(IntegerCharacter character, AttributeData impl) {
        super(character, impl);
    }
    
    @Override
    public IntegerCharacter getCharacter() {
        return (IntegerCharacter) super.getCharacter();
    }
    
    public Set<Integer> getPresentValues() {
        return _impl.getPresentStateOrIntegerValues();
    }
    
    public void setPresentValues(Set<Integer> values) {
        _impl.setPresentStateOrIntegerValues(values);
        notifyObservers();
    }
}
