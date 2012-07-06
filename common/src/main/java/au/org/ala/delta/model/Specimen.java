/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.model.impl.SimpleAttributeData;

public class Specimen {

    private DeltaDataSet _dataset;

    // If true, all character dependencies are ignored. This value is set to
    // true by KEY - it generates a key without regard for
    // character dependencies.
    boolean _ignoreCharacterDependencies;

    boolean _matchInapplicables;
    boolean _matchUnknowns;
    MatchType _matchType;

    // Use a linked hash map so that character can be returned in the
    // order that they were used.
    private LinkedHashMap<Character, Attribute> _characterValues;

    /**
     * Keeps a count of the number of times a character has been made
     * inapplicable - this is needed because a character can have more that one
     * character controlling it. If there is no entry for a character in this
     * map, or its entry is zero, then is it not inapplicable
     */
    private Map<Character, Integer> _characterInapplicabilityCounts;

    /**
     * Keeps a count of the number of times a character has been deemed
     * "maybe inapplicable" - i.e. a controlling character has been set with
     * state values that make the character inapplicable, but has also been set
     * with other state values. This is needed because a character can have more
     * that one character controlling it. If there is no entry for a character
     * in this map, or its entry is zero, then is it not inapplicable
     */
    private Map<Character, Integer> _characterMaybeInapplicableCounts;

    private Map<Item, Set<Character>> _taxonDifferences;

    public Specimen(DeltaDataSet dataset, boolean ignoreCharacterDependencies, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType) {
        _characterValues = new LinkedHashMap<Character, Attribute>();

        _dataset = dataset;

        _characterInapplicabilityCounts = new HashMap<Character, Integer>();
        _characterMaybeInapplicableCounts = new HashMap<Character, Integer>();

        _ignoreCharacterDependencies = ignoreCharacterDependencies;

        _matchInapplicables = matchInapplicables;
        _matchUnknowns = matchUnknowns;
        _matchType = matchType;

        // initialise the taxon differences table
        _taxonDifferences = new HashMap<Item, Set<Character>>();
        for (Item taxon : _dataset.getItemsAsList()) {
            _taxonDifferences.put(taxon, new HashSet<Character>());
        }
    }

    // Used to copy values out of an existing specimen and apply new match
    // settings
    public Specimen(DeltaDataSet dataset, boolean ignoreCharacterDependencies, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType, Specimen oldSpecimen) {
        this(dataset, ignoreCharacterDependencies, matchInapplicables, matchUnknowns, matchType);

        for (Character ch : oldSpecimen.getUsedCharacters()) {
            setAttributeForCharacter(ch, oldSpecimen.getAttributeForCharacter(ch));
        }
    }

    public boolean hasValueFor(Character ch) {
        return _characterValues.containsKey(ch);
    }

    public Attribute getAttributeForCharacter(Character ch) {
        if (_characterValues.containsKey(ch)) {
            return _characterValues.get(ch);
        } else {
            SimpleAttributeData impl = new SimpleAttributeData(true, isCharacterInapplicable(ch));
            Attribute attr = AttributeFactory.newAttribute(ch, impl);
            attr.setSpecimenAttribute(true);
            return attr;
        }
    }

    public void removeValueForCharacter(Character ch) {
        Attribute attrToRemove = _characterValues.get(ch);

        // Do nothing if no value recorded for the supplied character
        if (attrToRemove != null) {

            // IMPORTANT - differences table must be updated first, if
            // _characterValues
            // is modified first then the differences table will be updated
            // incorrectly!
            updateDifferencesTable(attrToRemove, true);

            _characterValues.remove(ch);

            // If this is a controlling character, also need to remove values
            // for
            // any dependent characters, and update inapplicable/maybe
            // inapplicable count
            if (!_ignoreCharacterDependencies) {
                for (CharacterDependency cd : ch.getDependentCharacters()) {
                    // This is a controlling character, so that value to remove
                    // must
                    // be multistate
                    MultiStateAttribute msAttr = (MultiStateAttribute) attrToRemove;

                    // Update inapplicable/maybe inapplicable state for
                    // dependant characters
                    for (int depCharId : cd.getDependentCharacterIds()) {
                        Character dependentChar = _dataset.getCharacter(depCharId);

                        boolean specimenValueForDependantChar = hasValueFor(dependentChar);

                        if (cd.getStates().equals(msAttr.getPresentStates())) {
                            // character was inapplicable
                            
                            //recursively decrement inapplicability count for dependant characters of this
                            //(parent) dependant character if there is no specimen value set for this (parent)
                            // dependant character. Otherwise, 
                            decrementInapplicabilityCount(dependentChar, !specimenValueForDependantChar);
                        } else if (msAttr.getPresentStates().containsAll(cd.getStates())) {
                            // character was maybe inapplicable
                            decrementMaybeInapplicableCount(dependentChar, !specimenValueForDependantChar);
                        }

                        // remove value for dependant character
                        if (specimenValueForDependantChar) {
                            removeValueForCharacter(dependentChar);
                        }
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

    public void setAttributeForCharacter(Character ch, Attribute attr) {
        if (!ch.equals(attr.getCharacter())) {
            throw new IllegalArgumentException(String.format("Invalid value for character %s", ch.toString()));
        }

        if (isCharacterInapplicable(ch)) {
            throw new IllegalArgumentException(String.format("Cannot set character %s - this character is inapplicable", ch.toString()));
        }

        // if the supplied value is identical to the current value for the
        // character, simply
        // remove and re-add the current value to the map. As this is a
        // LinkedHashMap, this will have the
        // effect of putting this value last in the result of
        // getUsedCharacters(). No other action is required.
        // if (hasValueFor(ch) && getAttributeForCharacter(ch).equals(attr)) {
        // Attribute currentValue = getAttributeForCharacter(ch);
        // _characterValues.remove(ch);
        // _characterValues.put(ch, currentValue);
        // return;
        // }

        if (hasValueFor(ch)) {
            Attribute oldValueForCharacter = _characterValues.get(ch);
            if (oldValueForCharacter != null) {
                // Remove as this will force the LinkedHashMap to return this
                // value last when
                // listing its keys. The keyset from the LinkedHashMap is used
                // in
                // getUsedCharacters()
                updateDifferencesTable(oldValueForCharacter, true);
                _characterValues.remove(ch);

                // update any characters that were made inapplicable or maybe
                // inapplicable by the old value set for this character.
                if (!_ignoreCharacterDependencies) {

                    for (CharacterDependency cd : ch.getDependentCharacters()) {
                        // This is a controlling character, so that value to
                        // remove
                        // must
                        // be multistate
                        MultiStateAttribute msAttr = (MultiStateAttribute) oldValueForCharacter;

                        for (int depCharId : cd.getDependentCharacterIds()) {
                            Character dependentChar = _dataset.getCharacter(depCharId);
                            if (cd.getStates().equals(msAttr.getPresentStates())) {
                                // character was inapplicable
                                decrementInapplicabilityCount(dependentChar, true);
                            } else if (msAttr.getPresentStates().containsAll(cd.getStates())) {
                                // character was maybe inapplicable
                                decrementMaybeInapplicableCount(dependentChar, true);
                            }
                        }
                    }
                }
            }
        }

        // if there are controlling characters, check that their values have
        // been set.
        if (!_ignoreCharacterDependencies) {
            for (CharacterDependency cd : ch.getControllingCharacters()) {
                Character controllingChar = _dataset.getCharacter(cd.getControllingCharacterId());
                if (!hasValueFor(controllingChar)) {
                    throw new IllegalStateException(String.format("Cannot set value for character %s - controlling character %s has not been set", ch.getCharacterId(),
                            controllingChar.getCharacterId()));
                }
            }
        }

        _characterValues.put(ch, attr);

        if (!_ignoreCharacterDependencies) {
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                // ch is a controlling character and therefore value must be a
                // multistate value
                MultiStateAttribute msAttr = (MultiStateAttribute) attr;

                for (int depCharId : cd.getDependentCharacterIds()) {
                    Character dependentChar = _dataset.getCharacter(depCharId);
                    if (cd.getStates().equals(msAttr.getPresentStates())) {
                        // character is inapplicable
                        removeValueForCharacter(dependentChar);
                        incrementInapplicabilityCount(dependentChar);
                    } else if (msAttr.getPresentStates().containsAll(cd.getStates())) {
                        // character is maybe inapplicable
                        incrementMaybeInapplicableCount(dependentChar);
                    }
                }
            }
        }

        updateDifferencesTable(attr, false);
    }

    /**
     * Decrement the inapplicability count for the supplied character, and
     * optionally for its dependant characters
     * 
     * @param ch
     *            the character in question
     * @param recurse
     *            if true, also decrement inapplicability count for dependant
     *            characters
     * 
     */
    private void decrementInapplicabilityCount(Character ch, boolean recurse) {
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

        if (!_ignoreCharacterDependencies && recurse) {
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                for (int depCharId : cd.getDependentCharacterIds()) {
                    decrementInapplicabilityCount(_dataset.getCharacter(depCharId), recurse);
                }
            }
        }
    }

    /**
     * Increment the inapplicability count for the supplied character and any
     * dependant characters
     * 
     * @param ch
     */
    private void incrementInapplicabilityCount(Character ch) {
        if (_characterInapplicabilityCounts.containsKey(ch)) {
            _characterInapplicabilityCounts.put(ch, _characterInapplicabilityCounts.get(ch) + 1);
        } else {
            _characterInapplicabilityCounts.put(ch, 1);
        }

        if (!_ignoreCharacterDependencies) {
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                for (int depCharId : cd.getDependentCharacterIds()) {
                    incrementInapplicabilityCount(_dataset.getCharacter(depCharId));
                }
            }
        }
    }

    /**
     * Decrement the maybe inapplicable count for the supplied character BUT NOT
     * FOR ANY DEPENDANT CHARACTERS
     * 
     * @param ch
     */
    private void decrementMaybeInapplicableCount(Character ch, boolean recurse) {
        if (_characterMaybeInapplicableCounts.containsKey(ch)) {
            int newCount = _characterMaybeInapplicableCounts.get(ch) - 1;

            if (newCount == 0) {
                _characterMaybeInapplicableCounts.remove(ch);
            } else {
                _characterMaybeInapplicableCounts.put(ch, newCount);
            }
        } else {
            throw new IllegalStateException(String.format("Character %s not maybe inapplicable", ch.getCharacterId()));
        }

        if (!_ignoreCharacterDependencies && recurse) {
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                for (int depCharId : cd.getDependentCharacterIds()) {
                    decrementMaybeInapplicableCount(_dataset.getCharacter(depCharId), recurse);
                }
            }
        }
    }

    /**
     * Increment the maybe inapplicable count for the supplied character and any
     * dependant characters
     * 
     * @param ch
     */
    private void incrementMaybeInapplicableCount(Character ch) {
        if (_characterMaybeInapplicableCounts.containsKey(ch)) {
            _characterMaybeInapplicableCounts.put(ch, _characterMaybeInapplicableCounts.get(ch) + 1);
        } else {
            _characterMaybeInapplicableCounts.put(ch, 1);
        }

        if (!_ignoreCharacterDependencies) {
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                for (int depCharId : cd.getDependentCharacterIds()) {
                    incrementMaybeInapplicableCount(_dataset.getCharacter(depCharId));
                }
            }
        }
    }

    private void updateDifferencesTable(Attribute specimenAttr, boolean removed) {
        List<Attribute> attrs = _dataset.getAllAttributesForCharacter(specimenAttr.getCharacter().getCharacterId());

        for (Item taxon : _dataset.getItemsAsList()) {
            boolean match = false;

            // Subtract 1 as taxa are 1 indexed in the dataset
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);

            if (specimenAttr instanceof MultiStateAttribute) {
                MultiStateAttribute msSpecimenAttr = (MultiStateAttribute) specimenAttr;
                MultiStateAttribute msAttr = (MultiStateAttribute) attr;
                match = DiffUtils.compareMultistate(msSpecimenAttr, msAttr, _matchUnknowns, _matchInapplicables, _matchType);
            } else if (specimenAttr instanceof IntegerAttribute) {
                IntegerAttribute intSpecimenAttr = (IntegerAttribute) specimenAttr;
                IntegerAttribute intAttr = (IntegerAttribute) attr;
                match = DiffUtils.compareInteger(intSpecimenAttr, intAttr, _matchUnknowns, _matchInapplicables, _matchType);
            } else if (specimenAttr instanceof RealAttribute) {
                RealAttribute realSpecimenAttr = (RealAttribute) specimenAttr;
                RealAttribute realAttr = (RealAttribute) attr;
                match = DiffUtils.compareReal(realSpecimenAttr, realAttr, _matchUnknowns, _matchInapplicables, _matchType);
            } else if (specimenAttr instanceof TextAttribute) {
                TextAttribute txtSpecimenAttr = (TextAttribute) specimenAttr;
                TextAttribute txtAttr = (TextAttribute) attr;
                match = DiffUtils.compareText(txtSpecimenAttr, txtAttr, _matchUnknowns, _matchInapplicables, _matchType);
            } else {
                throw new RuntimeException(String.format("Unrecognised CharacterValue subtype %s", specimenAttr.getClass().getName()));
            }

            Set<Character> differingCharacters = _taxonDifferences.get(taxon);

            if (removed && !match) {
                differingCharacters.remove(specimenAttr.getCharacter());
            } else if (!removed && !match) {
                differingCharacters.add(specimenAttr.getCharacter());
            }
        }
    }

    // No defensive copy for efficiency reasons. The returned map should not be
    // modified
    public Map<Item, Set<Character>> getTaxonDifferences() {
        return _taxonDifferences;
    }

    public Set<Character> getInapplicableCharacters() {
        return new HashSet<Character>(_characterInapplicabilityCounts.keySet());
    }

    public boolean isCharacterInapplicable(Character ch) {
        return _characterInapplicabilityCounts.containsKey(ch) && _characterInapplicabilityCounts.get(ch) > 0;
    }

    public boolean isCharacterMaybeInapplicable(Character ch) {
        // Inapplicable trumps maybe inapplicable, so if a character has been
        // made inapplicable by one of its controlling characters, it should be
        // considered inapplicable instead of
        // maybe inapplicable
        return !isCharacterInapplicable(ch) && _characterMaybeInapplicableCounts.containsKey(ch) && _characterMaybeInapplicableCounts.get(ch) > 0;
    }
}
