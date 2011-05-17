package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.intkey.model.CharacterComparator;
import au.org.ala.delta.model.Character;

public class Specimen {
    
    private Map<Character, CharacterValue> _characterValues;
    
    public Specimen() {
        _characterValues = new HashMap<Character, CharacterValue>();
    }
    
    public boolean hasValueFor(Character ch) {
        return _characterValues.containsKey(ch);
    }
    
    public void removeValueForCharacter(Character ch) {
        _characterValues.remove(ch);
    }
    
    public List<Character> getUsedCharacters() {
        List<Character> usedCharacters = new ArrayList<Character>(_characterValues.keySet());
        Collections.sort(usedCharacters, new CharacterComparator());
        return usedCharacters;
    }
    
    public void setValueForCharacter(Character ch, CharacterValue value) {
        if (!ch.equals(value.getCharacter())) {
            throw new IllegalArgumentException(String.format("Invalid value for character %s", ch.getDescription()));
        }
        
        _characterValues.put(ch, value);
    }
}
