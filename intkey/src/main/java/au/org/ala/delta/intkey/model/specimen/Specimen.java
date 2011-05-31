package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.rtf.AttributeValue;

public class Specimen {

    // TODO characters applicable/inapplicable

    private IntkeyDataset _dataset;

    boolean _matchInapplicables;
    boolean _matchUnknowns;
    MatchType _matchType;

    // Use a linked hash map so that character can be returned in the
    // order that they were used.
    private LinkedHashMap<Character, CharacterValue> _characterValues;

    private Map<Character, Boolean> _characterAvailability;

    private Map<Item, Integer> _taxonDifferences;

    public Specimen(IntkeyDataset dataset, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType) {
        _characterValues = new LinkedHashMap<Character, CharacterValue>();
        _characterAvailability = new HashMap<Character, Boolean>();
        _dataset = dataset;

        _matchInapplicables = matchInapplicables;
        _matchUnknowns = matchUnknowns;
        _matchType = matchType;
        
        _taxonDifferences = new HashMap<Item, Integer>();
    }

    public boolean hasValueFor(Character ch) {
        return _characterValues.containsKey(ch);
    }

    public void removeValueForCharacter(Character ch) {
        CharacterValue valToRemove = _characterValues.get(ch); 
        
        _characterValues.remove(ch);
        
        updateDifferencesTable(valToRemove, true);

        // If this is a controlling character, also need to remove values for
        // any dependent characters
        for (CharacterDependency cd : ch.getDependentCharacters()) {
            for (int dependentCharId : cd.getDependentCharacterIds()) {
                Character dependentCharacter = _dataset.getCharacter(dependentCharId);
                removeValueForCharacter(dependentCharacter);
            }
        }
    }

    /**
     * @return a list of characters that have been used, in the order that they
     *         were used.
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

        // if there are controlling characters, check that their values have
        // been set.
        for (CharacterDependency cd : ch.getControllingCharacters()) {
            Character controllingChar = _dataset.getCharacter(cd.getControllingCharacterId());
            if (!hasValueFor(controllingChar)) {
                throw new IllegalStateException(String.format("Cannot set value for character %s - controlling character %s has not been set", ch.getCharacterId(), controllingChar.getCharacterId()));
            }
        }

        // update character availability here

        _characterValues.put(ch, value);
        
        updateDifferencesTable(value, false);

        // if character was already set, may need to remove the set values for
        // dependent characters
        // based on the new value of this (controlling) character
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

            // if the value for the character now contains only inapplicable
            // states, then
            // we need to remove the set values for the dependent character (and
            // any characters that
            // the dependent character controls)
            for (CharacterDependency cd : immediateDependencies) {

                if (cd.getStates().containsAll(setStateValues)) {
                    for (int depCharId : cd.getDependentCharacterIds()) {
                        removeValueForCharacter(_dataset.getCharacter(depCharId));
                    }
                }
            }
        }
    }

    private void updateDifferencesTable(CharacterValue val, boolean removed) {
        List<Attribute> attrs = _dataset.getAttributesForCharacter(val.getCharacter().getCharacterId());

        for (Item taxon : _dataset.getTaxa()) {
            boolean match = false;

            //Subtract 1 as taxa are 1 indexed in the dataset
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);

            if (attr.isInapplicable()) {
                match = _matchInapplicables;
            } else if (attr.isUnknown()) {
                match = _matchUnknowns;
            } else {

                if (val instanceof MultiStateValue) {
                    MultiStateValue msVal = (MultiStateValue) val;
                    MultiStateAttribute msAttr = (MultiStateAttribute) attr;
                    match = compareMultistate(msVal, msAttr);
                } else if (val instanceof IntegerValue) {
                    IntegerValue intVal = (IntegerValue) val;
                    IntegerAttribute intAttr = (IntegerAttribute) attr;
                    match = compareInteger(intVal, intAttr);
                } else if (val instanceof RealValue) {
                    RealValue realVal = (RealValue) val;
                    RealAttribute realAttr = (RealAttribute) attr;
                    match = compareReal(realVal, realAttr);
                } else if (val instanceof TextValue) {
                    TextValue txtVal = (TextValue) val;
                    TextAttribute txtAttr = (TextAttribute) attr;
                    match = compareText(txtVal, txtAttr);
                } else {
                    throw new RuntimeException(String.format("Unrecognised CharacterValue subtype %s", val.getClass().getName()));
                }
            }

            if (!match) {
                int currentDiffCount = 0;
                if (_taxonDifferences.containsKey(taxon)) {
                    currentDiffCount = _taxonDifferences.get(taxon);
                }

                int newDiffCount = currentDiffCount;
                if (removed) {
                    newDiffCount--;
                } else {
                    newDiffCount++;
                }
                _taxonDifferences.put(taxon, newDiffCount);
            }
        }
        
        System.out.println(_taxonDifferences.size());
    }

    private boolean compareMultistate(MultiStateValue val, MultiStateAttribute attr) {
        boolean match = false;
        
        switch (_matchType) {
        case EXACT:
            match = val.getStateValues().equals(new ArrayList<Integer>(attr.getPresentStates()));
            break;
        case SUBSET:
            match = val.getStateValues().containsAll(attr.getPresentStates());
            break;
        case OVERLAP:
            for (int stateVal: val.getStateValues()) {
                if (attr.getPresentStates().contains(stateVal)) {
                    match = true;
                    break;
                }
            }
            break;
        default:
            throw new RuntimeException(String.format("Unrecognized match type %s", _matchType.toString()));
        }
        
        return match;
    }

    private boolean compareInteger(IntegerValue val, IntegerAttribute attr) {
        return false;
    }

    private boolean compareReal(RealValue val, RealAttribute attr) {
        return false;
    }

    private boolean compareText(TextValue val, TextAttribute attr) {
        return false;
    }
    
    public Map<Item, Integer> getTaxonDifferences() {
        //defensive copy
        return new HashMap(_taxonDifferences);
    }
}
