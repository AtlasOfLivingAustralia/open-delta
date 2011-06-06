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

    /**
     * Keeps a count of the number of times a character has been made
     * inapplicable - this is needed because a character can have more that one
     * character controlling it. If there is no entry for a character in this
     * map, or its entry is zero, then is it not inapplicable
     */
    private Map<Character, Integer> _characterInapplicabilityCounts;

    private Map<Item, Integer> _taxonDifferences;

    public Specimen(IntkeyDataset dataset, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType) {
        _characterValues = new LinkedHashMap<Character, CharacterValue>();

        _dataset = dataset;

        _characterInapplicabilityCounts = new HashMap<Character, Integer>();

        _matchInapplicables = matchInapplicables;
        _matchUnknowns = matchUnknowns;
        _matchType = matchType;
    }

    public boolean hasValueFor(Character ch) {
        return _characterValues.containsKey(ch);
    }

    public CharacterValue getValueForCharacter(Character ch) {
        return _characterValues.get(ch);
    }

    public void removeValueForCharacter(Character ch) {
        CharacterValue valToRemove = _characterValues.get(ch);

        // Do nothing if no value recorded for the supplied character
        if (valToRemove != null) {
            _characterValues.remove(ch);

            updateDifferencesTable(valToRemove, true);

            // If this is a controlling character, also need to remove values
            // for
            // any dependent characters
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                // This is a controlling character, so that value to remove must
                // be multistate
                MultiStateValue msVal = (MultiStateValue) valToRemove;

                for (int dependentCharId : cd.getDependentCharacterIds()) {
                    Character dependentCharacter = _dataset.getCharacter(dependentCharId);
                    removeValueForCharacter(dependentCharacter);

                    // If this character was inapplicable due to its value,
                    // update the inapplicablity count for it
                    // and its dependants
                    if (cd.getStates().containsAll(msVal.getStateValues())) {
                        processPreviouslyInapplicableCharacter(dependentCharacter);
                    }
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
        if (!ch.equals(value.getCharacter())) {
            throw new IllegalArgumentException(String.format("Invalid value for character %s", ch.getDescription()));
        }

        // initialise the taxon differences table if it has not already been
        // initialized.
        if (_taxonDifferences == null) {
            _taxonDifferences = new HashMap<Item, Integer>();
            for (Item taxon : _dataset.getTaxa()) {
                _taxonDifferences.put(taxon, 0);
            }
        }

        if (hasValueFor(ch)) {
            removeValueForCharacter(ch);
        }

        // if there are controlling characters, check that their values have
        // been set.
        for (CharacterDependency cd : ch.getControllingCharacters()) {
            Character controllingChar = _dataset.getCharacter(cd.getControllingCharacterId());
            if (!hasValueFor(controllingChar)) {
                throw new IllegalStateException(String.format("Cannot set value for character %s - controlling character %s has not been set", ch.getCharacterId(), controllingChar.getCharacterId()));
            }
        }

        _characterValues.put(ch, value);

        for (CharacterDependency cd : ch.getDependentCharacters()) {
            // ch is a controlling character and therefore value must be a
            // multistate value
            MultiStateValue msVal = (MultiStateValue) value;

            if (cd.getStates().containsAll(msVal.getStateValues())) {
                for (int depCharId : cd.getDependentCharacterIds()) {
                    Character dependentChar = _dataset.getCharacter(depCharId);
                    removeValueForCharacter(dependentChar);
                    processNewlyInapplicableCharacter(dependentChar);
                }
            }
        }

        updateDifferencesTable(value, false);
    }

    private void processPreviouslyInapplicableCharacter(Character ch) {
        if (_characterInapplicabilityCounts.containsKey(ch)) {
            int newCount = _characterInapplicabilityCounts.get(ch) - 1;

            if (newCount == 0) {
                _characterInapplicabilityCounts.remove(ch);
            } else {
                _characterInapplicabilityCounts.put(ch, newCount);
            }
        } else {
            throw new IllegalStateException(String.format("Character %s not inapplicable", ch.getCharacterId()));
        }

        for (CharacterDependency cd : ch.getDependentCharacters()) {
            for (int depCharId : cd.getDependentCharacterIds()) {
                processPreviouslyInapplicableCharacter(_dataset.getCharacter(depCharId));
            }
        }
    }

    private void processNewlyInapplicableCharacter(Character ch) {
        if (_characterInapplicabilityCounts.containsKey(ch)) {
            _characterInapplicabilityCounts.put(ch, _characterInapplicabilityCounts.get(ch) + 1);
        } else {
            _characterInapplicabilityCounts.put(ch, 1);
        }

        for (CharacterDependency cd : ch.getDependentCharacters()) {
            for (int depCharId : cd.getDependentCharacterIds()) {
                processNewlyInapplicableCharacter(_dataset.getCharacter(depCharId));
            }
        }
    }

    private void updateDifferencesTable(CharacterValue val, boolean removed) {
        List<Attribute> attrs = _dataset.getAttributesForCharacter(val.getCharacter().getCharacterId());

        for (Item taxon : _dataset.getTaxa()) {
            boolean match = false;

            // Subtract 1 as taxa are 1 indexed in the dataset
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);

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

            int currentDiffCount = 0;
            if (_taxonDifferences.containsKey(taxon)) {
                currentDiffCount = _taxonDifferences.get(taxon);
            }

            if (removed && match) {
                _taxonDifferences.put(taxon, Math.max(0, currentDiffCount - 1));
            } else if (!removed && !match) {
                _taxonDifferences.put(taxon, currentDiffCount + 1);
            }
        }
    }

    private boolean compareMultistate(MultiStateValue val, MultiStateAttribute attr) {
        if ((!hasValueFor(val.getCharacter()) && isInapplicable(val.getCharacter())) || (attr.isUnknown() && attr.isInapplicable())) {
            return _matchInapplicables;
        }

        if ((!hasValueFor(val.getCharacter()) && !isInapplicable(val.getCharacter())) || (attr.isUnknown() && !attr.isInapplicable())) {
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
        if ((!hasValueFor(val.getCharacter()) && isInapplicable(val.getCharacter())) || (attr.isUnknown() && attr.isInapplicable())) {
            return _matchInapplicables;
        }

        if ((!hasValueFor(val.getCharacter()) && !isInapplicable(val.getCharacter())) || (attr.isUnknown() && !attr.isInapplicable())) {
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
        if ((!hasValueFor(val.getCharacter()) && isInapplicable(val.getCharacter())) || (attr.isUnknown() && attr.isInapplicable())) {
            return _matchInapplicables;
        }

        if ((!hasValueFor(val.getCharacter()) && !isInapplicable(val.getCharacter())) || (attr.isUnknown() && !attr.isInapplicable())) {
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

        if ((!hasValueFor(val.getCharacter()) && isInapplicable(val.getCharacter())) || (attr.isUnknown() && attr.isInapplicable())) {
            return false;
        }

        if ((!hasValueFor(val.getCharacter()) && !isInapplicable(val.getCharacter())) || (attr.isUnknown() && !attr.isInapplicable())) {
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

        if (_characterValues.size() == 0) {
            throw new IllegalStateException("Cannot get taxon differences when no values have been set for specimen");
        }

        return new HashMap<Item, Integer>(_taxonDifferences);
    }

    public Set<Character> getAvailableCharacters() {
        Set<Character> retSet = new HashSet<Character>(_dataset.getCharacters());
        retSet.removeAll(_characterValues.keySet());
        retSet.removeAll(_characterInapplicabilityCounts.keySet());
        return retSet;
    }

    private boolean isInapplicable(Character ch) {
        return _characterInapplicabilityCounts.containsKey(ch) && _characterInapplicabilityCounts.get(ch) > 0;
    }
}
