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

    private Map<Item, Set<Character>> _taxonDifferences;

    public Specimen(DeltaDataSet dataset, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType) {
        _characterValues = new LinkedHashMap<Character, Attribute>();

        _dataset = dataset;

        _characterInapplicabilityCounts = new HashMap<Character, Integer>();

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
    public Specimen(DeltaDataSet dataset, boolean matchInapplicables, boolean matchUnknowns, MatchType matchType, Specimen oldSpecimen) {
        this(dataset, matchInapplicables, matchUnknowns, matchType);

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
            // any dependent characters
            for (CharacterDependency cd : ch.getDependentCharacters()) {
                // This is a controlling character, so that value to remove must
                // be multistate
                MultiStateAttribute msAttr = (MultiStateAttribute) attrToRemove;

                for (int dependentCharId : cd.getDependentCharacterIds()) {
                    Character dependentCharacter = _dataset.getCharacter(dependentCharId);
                    removeValueForCharacter(dependentCharacter);

                    // If this character was inapplicable due to its value,
                    // update the inapplicablity count for it
                    // and its dependants
                    if (cd.getStates().containsAll(msAttr.getPresentStates())) {
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

    public void setAttributeForCharacter(Character ch, Attribute attr) {
        if (!ch.equals(attr.getCharacter())) {
            throw new IllegalArgumentException(String.format("Invalid value for character %s", ch.toString()));
        }

        if (isCharacterInapplicable(ch)) {
            throw new IllegalArgumentException(String.format("Cannot set character %s - this character is inapplicable", ch.toString()));
        }

        // do nothing if the supplied value is identical to the current value
        // for
        // the character.
        if (hasValueFor(ch) && getAttributeForCharacter(ch).equals(attr)) {
            return;
        }

        if (hasValueFor(ch)) {
            removeValueForCharacter(ch);
        }

        // if there are controlling characters, check that their values have
        // been set.
//        for (CharacterDependency cd : ch.getControllingCharacters()) {
//            Character controllingChar = _dataset.getCharacter(cd.getControllingCharacterId());
//            if (!hasValueFor(controllingChar)) {
//                throw new IllegalStateException(String.format("Cannot set value for character %s - controlling character %s has not been set", ch.getCharacterId(), controllingChar.getCharacterId()));
//            }
//        }

        _characterValues.put(ch, attr);

        for (CharacterDependency cd : ch.getDependentCharacters()) {
            // ch is a controlling character and therefore value must be a
            // multistate value
            MultiStateAttribute msAttr = (MultiStateAttribute) attr;

            if (cd.getStates().containsAll(msAttr.getPresentStates())) {
                for (int depCharId : cd.getDependentCharacterIds()) {
                    Character dependentChar = _dataset.getCharacter(depCharId);
                    removeValueForCharacter(dependentChar);
                    processNewlyInapplicableCharacter(dependentChar);
                }
            }
        }

        updateDifferencesTable(attr, false);
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

            // int currentDiffCount = 0;
            // if (_taxonDifferences.containsKey(taxon)) {
            // currentDiffCount = _taxonDifferences.get(taxon);
            // }

            Set<Character> differingCharacters = _taxonDifferences.get(taxon);

            if (removed && !match) {
                // _taxonDifferences.put(taxon, Math.max(0, currentDiffCount -
                // 1));
                differingCharacters.remove(specimenAttr.getCharacter());
            } else if (!removed && !match) {
                // _taxonDifferences.put(taxon, currentDiffCount + 1);
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
}
