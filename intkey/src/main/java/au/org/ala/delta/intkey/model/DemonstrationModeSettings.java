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
package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;

public class DemonstrationModeSettings {

    private boolean _autoTolerance;
    private int _diagLevel;
    private DiagType _diagType;
    private boolean _charactersFixed;
    private List<Attribute> _fixedCharactersAttributes;
    private Set<Integer> _exactCharactersSet;
    private List<String> _imagePathLocations;
    private List<String> _infoPathLocations;
    private boolean _matchInapplicables;
    private boolean _matchUnknowns;
    private MatchType _matchType;
    private double _rbase;
    private int _stopBest;
    private int _tolerance;
    private double _varyWeight;

    // values set by DISPLAY directives
    private boolean _displayNumbering;
    private boolean _displayInapplicables;
    private boolean _displayUnknowns;
    private boolean _displayComments;
    private boolean _displayContinuous;
    private ImageDisplayMode _displayImagesMode;
    private boolean _displayKeywords;
    private boolean _displayScaled;
    private boolean _displayEndIdentify;
    private boolean _displayInput;

    private IntkeyCharacterOrder _characterOrder;

    // The taxon to be separated when using the SEPARATE character order.
    private int _taxonToSeparate;

    private Set<Integer> _includedCharacters;
    private Set<Integer> _includedTaxa;

    public DemonstrationModeSettings(IntkeyContext context) {
        _autoTolerance = context.isAutoTolerance();
        _diagLevel = context.getDiagLevel();
        _diagType = context.getDiagType();
        _charactersFixed = context.charactersFixed();
        if (_charactersFixed) {
            _fixedCharactersAttributes = new ArrayList<Attribute>();
            for (int charNum: context.getFixedCharactersList()) {
                Character ch = context.getDataset().getCharacter(charNum);
                Attribute attr = context.getSpecimen().getAttributeForCharacter(ch);
                _fixedCharactersAttributes.add(attr);
            }
        }

        _exactCharactersSet = new HashSet<Integer>();
        for (Character ch : context.getExactCharacters()) {
            _exactCharactersSet.add(ch.getCharacterId());
        }
        
        _imagePathLocations = context.getImagePaths();
        _infoPathLocations = context.getInfoPaths();
        _matchInapplicables = context.getMatchInapplicables();
        _matchUnknowns = context.getMatchUnknowns();
        _matchType = context.getMatchType();
        _rbase = context.getRBase();
        _stopBest = context.getStopBest();
        _tolerance = context.getTolerance();
        _varyWeight = context.getVaryWeight();

        _displayNumbering = context.displayNumbering();
        _displayInapplicables = context.displayInapplicables();
        _displayUnknowns = context.displayUnknowns();
        _displayComments = context.displayComments();
        _displayContinuous = context.displayContinuous();
        _displayImagesMode = context.getImageDisplayMode();
        _displayKeywords = context.displayKeywords();
        _displayScaled = context.displayScaled();
        _displayEndIdentify = context.displayEndIdentify();
        _displayInput = context.displayInput();

        _characterOrder = context.getCharacterOrder();
        _taxonToSeparate = context.getTaxonToSeparate();

        _includedCharacters = new HashSet<Integer>();
        for (Character ch : context.getIncludedCharacters()) {
            _includedCharacters.add(ch.getCharacterId());
        }

        _includedTaxa = new HashSet<Integer>();
        for (Item taxon : context.getIncludedTaxa()) {
            _includedTaxa.add(taxon.getItemNumber());
        }
        

    }

    public void loadIntoContext(IntkeyContext context) {
        context.setAutoTolerance(_autoTolerance);
        context.setDiagLevel(_diagLevel);
        context.setDiagType(_diagType);
        context.setImagePaths(_imagePathLocations);
        context.setInfoPaths(_infoPathLocations);
        context.setMatchSettings(_matchUnknowns, _matchInapplicables, _matchType);
        context.setRBase(_rbase);
        context.setStopBest(_stopBest);
        context.setTolerance(_tolerance);
        context.setVaryWeight(_varyWeight);
        context.setExactCharacters(_exactCharactersSet);

        context.setDisplayNumbering(_displayNumbering);
        context.setDisplayInapplicables(_displayInapplicables);
        context.setDisplayComments(_displayComments);
        context.setDisplayContinuous(_displayContinuous);
        context.setImageDisplayMode(_displayImagesMode);
        context.setDisplayKeywords(_displayKeywords);
        context.setDisplayScaled(_displayScaled);
        context.setDisplayEndIdentify(_displayEndIdentify);
        context.setDisplayInput(_displayInput);
        context.setDisplayUnknowns(_displayUnknowns);

        switch (_characterOrder) {
        case BEST:
            context.setCharacterOrderBest();
            break;
        case NATURAL:
            context.setCharacterOrderNatural();
            break;
        case SEPARATE:
            context.setCharacterOrderSeparate(_taxonToSeparate);
            break;
        default:
            throw new IllegalArgumentException("Unrecognized character order");
        }
        
        context.setIncludedCharacters(_includedCharacters);
        context.setIncludedTaxa(_includedTaxa);

        if (_charactersFixed) {
            // sort fixed character attributes to that attributes for controlling characters appear
            // in the list before attributes for any dependent characters
            Collections.sort(_fixedCharactersAttributes, new ControllingCharacterAttributeComparator(context));
            context.setFixedCharactersFromAttributes(_fixedCharactersAttributes);
        }
    }
    
    private class ControllingCharacterAttributeComparator implements Comparator<Attribute> {

        private IntkeyContext _context;
        
        public ControllingCharacterAttributeComparator(IntkeyContext context) {
            _context = context;
        }
        
        @Override
        public int compare(Attribute attr1, Attribute attr2) {
            Character c1 = attr1.getCharacter();
            Character c2 = attr2.getCharacter();
            List<Character> c1ControllingChars = getAllControllingCharactersForCharacter(c1);
            List<Character> c2ControllingChars = getAllControllingCharactersForCharacter(c2);
            
            if (c2ControllingChars.contains(c1)) {
                return -1;
            } else if (c1ControllingChars.contains(c2)) {
                return 1;
            } else {
                return 0;                
            }
        }
        
        private List<Character> getAllControllingCharactersForCharacter(Character ch) {
            List<Character> controllingCharacters = new ArrayList<Character>();
            for (CharacterDependency charDep: ch.getControllingCharacters()) {
                Character cc = _context.getDataset().getCharacter(charDep.getControllingCharacterId());
                controllingCharacters.add(cc);
                controllingCharacters.addAll(getAllControllingCharactersForCharacter(cc));
            }
            
            return controllingCharacters;
        }
        
    }
}
