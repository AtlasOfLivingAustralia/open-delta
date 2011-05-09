package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.CharacterComparator;
import au.org.ala.delta.model.Character;
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
        _textValues.put(ch, val);
    }
    
    public boolean hasValueFor(Character ch) {
        if (ch instanceof MultiStateCharacter) {
            return _multistateValues.containsKey(ch);
        } else if (ch instanceof IntegerCharacter) {
            return _integerValues.containsKey(ch);
        } else if (ch instanceof RealCharacter) {
            return _realValues.containsKey(ch);
        } else if (ch instanceof TextCharacter) {
            return _textValues.containsKey(ch);
        } else {
            throw new RuntimeException("Unrecognized character type");
        }
    }
    
    public void removeValueForCharacter(Character ch) {
        if (ch instanceof MultiStateCharacter) {
            _multistateValues.remove(ch);
        } else if (ch instanceof IntegerCharacter) {
            _integerValues.remove(ch);
        } else if (ch instanceof RealCharacter) {
            _realValues.remove(ch);
        } else if (ch instanceof TextCharacter) {
            _textValues.remove(ch);
        } else {
            throw new RuntimeException("Unrecognized character type");
        }        
    }
    
    public List<Character> getUsedCharacters() {
        List<Character> usedCharacters = new ArrayList<Character>();
        usedCharacters.addAll(_integerValues.keySet());
        usedCharacters.addAll(_multistateValues.keySet());
        usedCharacters.addAll(_realValues.keySet());
        usedCharacters.addAll(_textValues.keySet());
        Collections.sort(usedCharacters, new CharacterComparator());
        return usedCharacters;
    }
}
