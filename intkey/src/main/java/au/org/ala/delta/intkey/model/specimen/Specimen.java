package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;

public class Specimen {
    
    IntkeyDataset _dataset;
    
    // Use a linked hash map so that character can be returned in the
    // order that they were used.
    private LinkedHashMap<Character, CharacterValue> _characterValues;
    
    private Map<Character, Boolean> _characterAvailability;
    
    public Specimen(IntkeyDataset dataset) {
        _characterValues = new LinkedHashMap<Character, CharacterValue>();
        _characterAvailability = new HashMap<Character, Boolean>();
        _dataset = dataset;
    }
    
    public boolean hasValueFor(Character ch) {
        return _characterValues.containsKey(ch);
    }
    
    public void removeValueForCharacter(Character ch) {
        _characterValues.remove(ch);

        //If this is a controlling character, also need to remove values for any dependent characters
        for (CharacterDependency cd: ch.getDependentCharacters()) {
            for (int dependentCharId: cd.getDependentCharacterIds()) {
                Character dependentCharacter = _dataset.getCharacter(dependentCharId);
                removeValueForCharacter(dependentCharacter);
            }
        }
    }
    
    /**
     * @return a list of characters that have been used, in the order that they were
     * used.
     */
    public List<Character> getUsedCharacters() {
        List<Character> usedCharacters = new ArrayList<Character>(_characterValues.keySet());
        return usedCharacters;
    }
    
    public void setValueForCharacter(Character ch, CharacterValue value) {
        boolean alreadySet = hasValueFor(ch);
        
        if (!ch.equals(value.getCharacter())) {
            throw new IllegalArgumentException(String.format("Invalid value for character %s", ch.getDescription()));
        }
        
        //if there are controlling characters, check that their values have been set.
        for (CharacterDependency cd: ch.getControllingCharacters()) {
            Character controllingChar = _dataset.getCharacter(cd.getControllingCharacterId());
            if (!hasValueFor(controllingChar)) {
                throw new IllegalStateException(String.format("Cannot set value for character %s - controlling character %s has not been set", ch.getCharacterId(), controllingChar.getCharacterId()));
            }
        }
        
        //update character availability here
        
        
        _characterValues.put(ch, value);
        
        //if character was already set, may need to remove the set values for dependent characters
        //based on the new value of this (controlling) character
        if (alreadySet) {
            processDependentCharacters(ch, value);
        }
    }
    
    public CharacterValue getValueForCharacter(Character ch) {
        return _characterValues.get(ch);
    }
    
    public boolean isCharacterAvailable(Character ch) {
        Boolean available = _characterAvailability.get(ch);
        
        if (available == null) {
            return false;
        }
        
        return available;
    }
    
    private void processDependentCharacters(Character ch, CharacterValue val) {
        List<CharacterDependency> immediateDependencies = ch.getDependentCharacters();

        if (immediateDependencies.size() > 0) {
            // ch is a controlling character and therefore must be multi
            // state.
            MultiStateValue multiStateVal = (MultiStateValue) val;
            List<Integer> setStateValues = multiStateVal.getStateValues();

            // if the value for the character contains now contains only inapplicable states, then
            // we need to remove the set values for the dependent character (and any characters that
            // the dependent character controls)
            for (CharacterDependency cd : immediateDependencies) {
                
                if (cd.getStates().containsAll(setStateValues)) {
                    for (int depCharId: cd.getDependentCharacterIds()) {
                        removeValueForCharacter(_dataset.getCharacter(depCharId));
                    }
                }
            }
        }

    }
}
