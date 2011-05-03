package au.org.ala.delta.intkey.model.specimen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class Specimen {
    
    private Map<IntegerCharacter, IntRange> _integerValues;
    private Map<RealCharacter, FloatRange> _realValues;
    private Map<MultiStateCharacter, List<Integer>> _multistateValues;
    private Map<TextCharacter, List<String>> _textValues;
    
    public Specimen() {
        _integerValues = new HashMap<IntegerCharacter, IntRange>();
        _realValues = new HashMap<RealCharacter, FloatRange>();
        _multistateValues = new HashMap<MultiStateCharacter, List<Integer>>();
        _textValues = new HashMap<TextCharacter, List<String>>();
    }
    
    public IntRange getIntegerValue(IntegerCharacter ch) {
        return null;
    }
    
    public void setIntegerValue(IntegerCharacter ch, IntRange val) {
        System.out.println("Setting integer character value: " + val.toString());
    }
    
    public FloatRange getRealValue(RealCharacter ch) {
        return null;
    }
    
    public void setRealValue(RealCharacter ch, FloatRange val) {
        System.out.println("Setting real character value: " + val.toString());
    }
    
    public List<Integer> getMultistateValue(MultiStateCharacter ch) {
        return null;
    }
    
    public void setMultiStateValue(MultiStateCharacter ch, List<Integer> val) {
        System.out.println("Setting multistate character value: " + val.toString());
    }
    
    public TextValue getTextValue(TextCharacter ch) {
        return null;
    }
    
    public void setTextValue(TextCharacter ch, List<String> val) {
        System.out.println("Setting text character value: " + val.toString());
    }
}
