package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

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

public class Specimen {

    // TODO characters applicable/inapplicable

    private IntkeyDataset _dataset;

    boolean _matchInapplicables;
    boolean _matchUnknowns;
    MatchType _matchType;

    // Use a linked hash map so that character can be returned in the
    // order that they were used.
    private LinkedHashMap<Character, CharacterValue> _characterValues;

    private Set<Character> _availableCharacters;

    private Map<Item, Integer> _taxonDifferences;

    public Specimen(IntkeyDataset dataset, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType) {
        _characterValues = new LinkedHashMap<Character, CharacterValue>();

        _dataset = dataset;

        // initially all characters are available
        _availableCharacters = new HashSet<Character>(_dataset.getCharacters());

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

        // Do nothing if no value recorded for the supplied character
        if (valToRemove != null) {
            _characterValues.remove(ch);

            updateDifferencesTable(valToRemove, true);

            // removed character is be placed back in the list of available
            // characters
            _availableCharacters.add(ch);

            // If this is a controlling character, also need to remove values
            // for
            // any dependent characters
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                for (int dependentCharId : cd.getDependentCharacterIds()) {
                    Character dependentCharacter = _dataset.getCharacter(dependentCharId);
                    removeValueForCharacter(dependentCharacter);
                }
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

        // used character is no longer available
        _availableCharacters.remove(ch);

        _characterValues.put(ch, value);

        updateDifferencesTable(value, false);

        processDependentCharacters(ch, value);
    }

    public CharacterValue getValueForCharacter(Character ch) {
        return _characterValues.get(ch);
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
                        Character dependentChar = _dataset.getCharacter(depCharId);
                        processInapplicableDependentCharacter(dependentChar);
                    }
                }
            }
        }
    }
    
    private void processInapplicableDependentCharacter(Character ch) {
        removeValueForCharacter(ch);

        // remove the character from the set of available
        // characters
        _availableCharacters.remove(ch);
        
        for (CharacterDependency cd: ch.getDependentCharacters()) {
            for (int depCharId : cd.getDependentCharacterIds()) {
                Character dependentChar = _dataset.getCharacter(depCharId);
                processInapplicableDependentCharacter(dependentChar);
            }
        }
    }

    private void updateDifferencesTable(CharacterValue val, boolean removed) {
        List<Attribute> attrs = _dataset.getAttributesForCharacter(val.getCharacter().getCharacterId());

        for (Item taxon : _dataset.getTaxa()) {
            boolean match = false;

            // Subtract 1 as taxa are 1 indexed in the dataset
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
    }

    private boolean compareMultistate(MultiStateValue val, MultiStateAttribute attr) {
        if (attr.isInapplicable()) {
            return _matchInapplicables;
        }

        if (attr.isUnknown()) {
            return _matchUnknowns;
        }

        boolean match = false;

        switch (_matchType) {
        case EXACT:
            match = val.getStateValues().equals(new ArrayList<Integer>(attr.getPresentStates()));
            break;
        case SUBSET:
            match = val.getStateValues().containsAll(attr.getPresentStates());
            break;
        case OVERLAP:
            for (int stateVal : val.getStateValues()) {
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
        if (attr.isInapplicable()) {
            return _matchInapplicables;
        }

        if (attr.isUnknown()) {
            return _matchUnknowns;
        }

        boolean match = false;

        List<Integer> valList = new ArrayList<Integer>();
        for (int i : val.getRange().toArray()) {
            valList.add(i);
        }

        switch (_matchType) {
        case EXACT:
            match = valList.equals(new ArrayList<Integer>(attr.getPresentValues()));
            break;
        case SUBSET:
            match = valList.containsAll(attr.getPresentValues());
            break;
        case OVERLAP:
            for (int intVal : valList) {
                if (attr.getPresentValues().contains(intVal)) {
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

    private boolean compareReal(RealValue val, RealAttribute attr) {
        if (attr.isInapplicable()) {
            return _matchInapplicables;
        }

        if (attr.isUnknown()) {
            return _matchUnknowns;
        }

        FloatRange valRange = val.getRange();
        FloatRange attrRange = attr.getPresentRange();

        boolean match = false;

        switch (_matchType) {
        case EXACT:
            match = valRange.equals(attrRange);
            break;
        case SUBSET:
            match = attrRange.containsRange(valRange);
            break;
        case OVERLAP:
            match = valRange.overlapsRange(attrRange);
            break;
        default:
            throw new RuntimeException(String.format("Unrecognized match type %s", _matchType.toString()));
        }

        return match;
    }

    /**
     * compares two text characters applying the following rules - 1. MATCH
     * INAPPLICABLE and MATCH UNKNOWN are ignored. Inapplicables and unknowns
     * are treated as a mismatch. 2. The text to be found may consist of a
     * number of sub-strings separated by '/'. In the cases of MATCH EXACT and
     * MATCH SUBSET, each sub-string must exist separately in the searched text.
     * For MATCH OVERLAP, the presence of any sub-string will result in a match.
     * 
     * @param val
     * @param attr
     * @return
     */
    private boolean compareText(TextValue val, TextAttribute attr) {

        if (attr.isInapplicable() || attr.isUnknown()) {
            return false;
        }

        boolean match = false;

        // Remove surrounding angle brackets from attribute text.
        String txtAttr = attr.getText().substring(1, attr.getText().length() - 1).toLowerCase();

        switch (_matchType) {
        case EXACT:
        case SUBSET:
            match = true;
            for (String txtVal : val.getValues()) {
                if (!txtAttr.contains(txtVal.toLowerCase())) {
                    match = false;
                    break;
                }
            }
            break;
        case OVERLAP:
            for (String txtVal : val.getValues()) {
                if (txtAttr.contains(txtVal.toLowerCase())) {
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

    public Map<Item, Integer> getTaxonDifferences() {
        // defensive copy
        return new HashMap<Item, Integer>(_taxonDifferences);
    }

    public Set<Character> getAvailableCharacters() {
        return new HashSet<Character>(_availableCharacters);
    }
}
