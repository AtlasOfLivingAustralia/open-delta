package au.org.ala.delta.intkey.model.specimen;

import java.util.Map;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class Specimen {
    
    private Map<IntegerCharacter, IntegerValue> _integerValues;
    private Map<RealCharacter, RealValue> _realValues;
    private Map<MultiStateCharacter, MultiStateValue> _multistateValues;
    private Map<TextCharacter, TextValue> _textValues;
    
    public IntegerValue getIntegerValue(IntegerCharacter ch) {
        return null;
    }
    
    public void setIntegerValue(IntegerCharacter ch, IntegerValue val) {
        
    }
    
    public RealValue getRealValue(RealCharacter ch) {
        return null;
    }
    
    public void setRealValue(RealCharacter ch, RealValue val) {
        
    }
    
    public MultiStateValue getMultistateValue(MultiStateCharacter ch) {
        return null;
    }
    
    public void setMultiStateValue(MultiStateCharacter ch, MultiStateValue val) {
        
    }
    
    public TextValue getTextValue(TextCharacter ch) {
        return null;
    }
    
    public void setTextValue(TextCharacter ch, TextValue val) {
        
    }
}
