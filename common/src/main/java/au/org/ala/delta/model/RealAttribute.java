package au.org.ala.delta.model;


import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.impl.AttributeData;

public class RealAttribute extends NumericAttribute {

    public RealAttribute(RealCharacter character, AttributeData impl) {
        super(character, impl);
    }
    
    @Override
    public RealCharacter getCharacter() {
        return (RealCharacter) super.getCharacter();
    }

    /**
     * An implicit value is one for which no attribute value is coded but an implicit value
     * has been specified for the attributes character.
     * @return true if the value of this attribute is derived from the Characters implicit value.
     */
    public boolean isImplicit() {
        return false;
    }
    
    public FloatRange getPresentRange() {
        return _impl.getRealRange();
    }
    
    public void setPresentRange(FloatRange range) {
        _impl.setRealRange(range);
    }

}
