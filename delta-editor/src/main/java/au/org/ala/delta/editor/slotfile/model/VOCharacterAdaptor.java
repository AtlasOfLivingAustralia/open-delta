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
package au.org.ala.delta.editor.slotfile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import au.org.ala.delta.editor.slotfile.*;
import au.org.ala.delta.model.attribute.ChunkType;
import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.CircularDependencyException;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.impl.CharacterData;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.model.impl.ControllingInfo.ControlledStateType;

/**
 * Adapts the CharacterData interface to the VOCharBaseDesc and VOCharTextDesc
 * slot file classes.
 */
public class VOCharacterAdaptor extends ImageHolderAdaptor implements CharacterData {

    /**
     * If they've been specified, units are stored as state text for state
     * number 1.
     */
    private static final int UNITS_TEXT_STATE_NUMBER = 1;

    private static final int VOUID_NULL = 0;

    private VOCharBaseDesc _charDesc;
    private VOCharTextDesc _textDesc;
    private DeltaVOP _vop;

    public VOCharacterAdaptor(DeltaVOP vop, VOCharBaseDesc charBase, VOCharTextDesc textDesc) {
        _vop = vop;
        _charDesc = charBase;
        _textDesc = textDesc;
    }
    
    @Override
    public int getNumber() {
    	return _vop.getDeltaMaster().charNoFromUniId(_charDesc.getUniId());
    }
    
    public void setNumber(int number) {
    	// NOP - numbers are managed by the VOP 
    	throw new RuntimeException("InternalError: setNumber called on VOCharacterAdapter");
    }

    @Override
    public String getDescription() {
        synchronized (_vop) {
            String description = "";
            if (_textDesc != null) {
                description = _textDesc.readFeatureText(TextType.RTF);
            }
            return description;
        }
    }

    @Override
    public void setDescription(String desc) {
        synchronized (_vop) {
            _textDesc.makeTemp();
            _textDesc.writeFeatureText(desc);
        }
    }

    @Override
    public void setUnits(String units) {
        synchronized (_vop) {
            _charDesc.setInitialStateNumber(1);
            int stateId = _charDesc.uniIdFromStateNo(1);
            _textDesc.makeTemp();
            _textDesc.writeStateText(units, stateId);
        }
    }

    @Override
    public boolean isExclusive() {
        synchronized (_vop) {
            return _charDesc.testCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE);
        }
    }

    @Override
    public void setExclusive(boolean b) {
        synchronized (_vop) {
            if (b) {
                _charDesc.setCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE);
            } else {
                _charDesc.clearCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE);
            }
        }
    }

    @Override
    public boolean isMandatory() {
        synchronized (_vop) {
            return _charDesc.testCharFlag(VOCharBaseDesc.CHAR_MANDATORY);
        }
    }

    @Override
    public void setMandatory(boolean b) {
        synchronized (_vop) {
            if (b) {
                _charDesc.setCharFlag(VOCharBaseDesc.CHAR_MANDATORY);
            } else {
                _charDesc.clearCharFlag(VOCharBaseDesc.CHAR_MANDATORY);
            }
        }
    }

    public VOCharBaseDesc getCharBaseDesc() {
        return _charDesc;
    }

    @Override
    public String getUnits() {
        synchronized (_vop) {
            String units = "";
            if (_charDesc.getNStatesUsed() >= UNITS_TEXT_STATE_NUMBER) {
                units = getStateText(UNITS_TEXT_STATE_NUMBER);
            }
            return units;
        }
    }

    @Override
    public String getStateText(int stateNumber) {
        synchronized (_vop) {
            if (_textDesc == null) {
                return "";
            }
            int stateId = _charDesc.uniIdFromStateNo(stateNumber);
            return _textDesc.readStateText(stateId, TextType.RTF);
        }
    }

    @Override
    public int getNumberOfStates() {
        synchronized (_vop) {
            // Trying to read past the number of states actually used yields an
            // error
            return _charDesc.getNStatesUsed();
        }
    }

    @Override
    public void setStateText(int stateNumber, String text) {
        synchronized (_vop) {
            int stateId = _charDesc.uniIdFromStateNo(stateNumber);
            _textDesc.writeStateText(text, stateId);
        }
    }

    @Override
    public void setNumberOfStates(int numStates) {
        synchronized (_vop) {
            if (_charDesc.getNStatesUsed() > 0) {
                throw new NotImplementedException("Ooops, don't currently handle deleting existing states...");
            }
            _charDesc.setInitialStateNumber(numStates);
        }
    }

    @Override
    public String getNotes() {
        synchronized (_vop) {
            return _textDesc.readNoteText(TextType.RTF);
        }
    }

    @Override
    public void setNotes(String note) {
        synchronized (_vop) {
            _textDesc.writeNoteText(note);
        }
    }

    @Override
    public int getCodedImplicitState() {
        synchronized (_vop) {
            return _charDesc.stateNoFromUniId(_charDesc.getCodedImplicit());
        }
    }

    @Override
    public int getUncodedImplicitState() {
        synchronized (_vop) {
            return _charDesc.stateNoFromUniId(_charDesc.getUncodedImplicit());
        }
    }

    @Override
    public void setCodedImplicitState(int stateNo) {
        synchronized (_vop) {
            int stateId = _charDesc.uniIdFromStateNo(stateNo);
            _charDesc.setCodedImplicit((short) stateId);
        }

    }

    @Override
    public void setUncodedImplicitState(int stateNo) {
        synchronized (_vop) {
            int stateId = _charDesc.uniIdFromStateNo(stateNo);
            _charDesc.setUncodedImplicit((short) stateId);
        }
    }

    @Override
    public void validateAttributeText(String text, ControllingInfo controlled) {
        synchronized (_vop) {
            Attribute attribute = new Attribute(text, _charDesc);
            if (controlled.isInapplicable()) {
                for (AttrChunk chunk : attribute)  {
                    int chunkType = chunk.getType();
                    if (chunkType != ChunkType.CHUNK_INAPPLICABLE &&
                            chunkType != ChunkType.CHUNK_TEXT &&
                            chunkType != ChunkType.CHUNK_LONGTEXT &&
                            // If "Unknown or inapplicable, allow Unknown and OR.
                            !((chunkType == ChunkType.CHUNK_UNKNOWN || chunkType == ChunkType.CHUNK_OR) && controlled.isStrictlyInapplicable())) {

                        throw new Attribute.AttributeParseException(Attribute.AttributeParseError.EAP_IS_INAPPLICABLE, -1);
                    }
                }
            }

        }
    }

    class CompareCharNos implements Comparator<Integer> {

        private boolean _compareStates;

        public CompareCharNos(boolean compareStates) {
            _compareStates = compareStates;
        }

        @Override
        public int compare(Integer leftNo, Integer rightNo) {
            VOControllingDesc left = getDescFromId(leftNo);
            VOControllingDesc right = getDescFromId(rightNo);
            return compare(left, right);

        }

        public int compare(VOControllingDesc left, VOControllingDesc right) {
            int leftNo = ((DeltaVOP) _charDesc.getVOP()).getDeltaMaster().charNoFromUniId(left.getCharId());
            int rightNo = ((DeltaVOP) _charDesc.getVOP()).getDeltaMaster().charNoFromUniId(right.getCharId());
            if (leftNo == rightNo && _compareStates) {
                throw new NotImplementedException();
            } else {
                return leftNo - rightNo;
            }
        }

    }

    @SuppressWarnings("unchecked")
    protected <T extends VOAnyDesc> T getDescFromId(int uniId) {
        return (T) ((DeltaVOP) _charDesc.getVOP()).getDescFromId(uniId);
    }

    protected VODeltaMasterDesc getDeltaMaster() {
        return ((DeltaVOP) _charDesc.getVOP()).getDeltaMaster();
    }

    @Override
    public ControllingInfo checkApplicability(Item itemModel) {
        synchronized (_vop) {
            VOItemDesc item = ((VOItemAdaptor) itemModel.getItemData()).getItemDesc();
            return checkApplicability(item, _charDesc, 0, new ArrayList<Integer>());
        }
    }

    protected ControllingInfo checkApplicability(VOItemDesc item, VOCharBaseDesc charBase, int recurseLevel, List<Integer> testedControlledChars) {

        int controllingId = 0;
        if (item == null || charBase == null || charBase.getNControlling() == 0) {
            return new ControllingInfo();
        }

        boolean unknownOk = false;
        List<Integer> controlling = charBase.readControllingInfo();

        if (controlling != null && controlling.size() > 1) {
            Collections.sort(controlling, new CompareCharNos(false));
        }

        List<Integer> controllingChars = new ArrayList<Integer>();
        SortedSet<Integer> controllingStates = new TreeSet<Integer>();
        List<Integer> newContStates = new ArrayList<Integer>();
        int testCharId = VOUID_NULL;

        controlling.add(VOUID_NULL); // Append dummy value, to ease handling of
                                     // the last element
        // Loop through all controlling attributes which directly control this
        // character...
        for (Integer i : controlling) {
            int newContCharId = 0;
            if (i == VOUID_NULL) {
                newContCharId = VOUID_NULL;
            } else {
                VOControllingDesc contAttrDesc = (VOControllingDesc) ((DeltaVOP) charBase.getVOP()).getDescFromId(i);
                newContStates = contAttrDesc.readStateIds();
                Collections.sort(newContStates);
                newContCharId = contAttrDesc.getCharId();
            }

            if (newContCharId == testCharId) {
                // / Build up all relevant controlling attributes under the
                // control of a
                // / single controlling character, merging the state lists as we
                // go....
                controllingStates.addAll(newContStates);
            } else {
                // Do checks when changing controlling state
                if (testCharId != VOUID_NULL) {
                    VOCharBaseDesc testCharBase = getDescFromId(testCharId);

                    if (!CharType.isMultistate(testCharBase.getCharType())) {
                        throw new RuntimeException("Controlling characters must be multistate!");
                    }
                    controllingId = testCharId;

                    // If the controlling character is coded, see whether it
                    // makes us inapplicable

                    if (item.hasAttribute(controllingId)) {
                        Attribute attrib = item.readAttribute(controllingId);
                        List<Integer> codedStates = new ArrayList<Integer>();
                        short[] pseudoValues = new short[] { 0 };
                        attrib.getEncodedStates(testCharBase, codedStates, pseudoValues);

                        // If controlling character is "variable", we are NOT
                        // controlled
                        if ((pseudoValues[0] & VOItemDesc.PSEUDO_VARIABLE) == 0) {
                            if (codedStates.isEmpty()) {
                                // If there are no states for the controlling
                                // character,
                                // but it is explicitly coded with the "unknown"
                                // pseudo-value,
                                // allow the controlled character to also be
                                // unknown.
                                if ((pseudoValues[0] & VOItemDesc.PSEUDO_UNKNOWN) != 0) {
                                    unknownOk = true;
                                } else {
                                    return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
                                }
                            } else if (controllingStates.containsAll(codedStates)) {
                                return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
                            }
                        }
                    } else if (testCharBase.getUncodedImplicit() != VOCharBaseDesc.STATEID_NULL) {
                        // if the controlling character is not encoded, see if
                        // there is an implicit value for it
                        if (controllingStates.contains(testCharBase.getUncodedImplicit())) {
                            return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
                        }
                    } else {
                        return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
                        // /// This should probably be handled as a somewhat
                        // special case,
                        // /// so the user can be pointed in the right direction
                    }
                }
                controllingId = VOUID_NULL;
                testCharId = newContCharId;
                if (testCharId != VOUID_NULL) {
                    controllingChars.add(testCharId);
                }
                controllingStates.clear();
                controllingStates.addAll(newContStates);
            }
        }

        // Up to this point, nothing has made this character inapplicable.
        // But it is possible that one of the controlling characters has itself
        // been made inapplicable.

        // Is this check really necessary? I suppose it is, but it slows things
        // down...
        for (int j : controllingChars) {

            if (++recurseLevel >= getDeltaMaster().getNContAttrs()) {
                try {
                    List<Integer> contChars = new ArrayList<Integer>();
                    getControlledChars(testedControlledChars, _charDesc, contChars, true);
                } catch (CircularDependencyException ex) {
                    return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
                }
            }
            VOCharBaseDesc testCharBase = getDescFromId(j);
            ControllingInfo info = checkApplicability(item, testCharBase, recurseLevel, testedControlledChars);
            if (info.isInapplicable()) {
                return info;
            }
        }
        return unknownOk ? new ControllingInfo(ControlledStateType.InapplicableOrUnknown, controllingId) : new ControllingInfo();
    }

    @Override
    public List<Integer> getControlledCharacterNumbers(boolean indirect) {
        synchronized (_vop) {
            List<Integer> controlledCharacterIds = new ArrayList<Integer>();
            List<Integer> testedCharacterIds = new ArrayList<Integer>();

            getControlledChars(testedCharacterIds, _charDesc, controlledCharacterIds, indirect);

            List<Integer> controlledCharacterNos = new ArrayList<Integer>();
            for (int id : controlledCharacterIds) {
                controlledCharacterNos.add(getVOP().getDeltaMaster().charNoFromUniId(id));
            }
            return controlledCharacterNos;
        }
    }

    private boolean getControlledChars(List<Integer> testedControlling, VOCharBaseDesc charBase, List<Integer> contChars, boolean includeIndirect) {
        return getControlledChars(testedControlling, charBase, contChars, includeIndirect, 0);
    }

    private boolean getControlledChars(List<Integer> testedControlling, VOCharBaseDesc charBase, List<Integer> contChars, boolean includeIndirect, int baseId) {
        // We maintain a list of "controlling" characters that have been (or,
        // rather, are being)
        // tested. This can prevent the infinite recursion which can otherwise
        // result if "circular" dependencies somehow are formed.

        if (testedControlling.contains(charBase.getUniId())) {
            return false;
        } else {
            testedControlling.add(charBase.getUniId());
            List<Integer> contAttrVector = charBase.readDependentContAttrs();
            if (contAttrVector.size() > 0) {
                // / Loop though all the controlling attributes "owned" by this
                // character
                for (Integer iter : contAttrVector) {
                    VOControllingDesc contAttrDesc = getDescFromId(iter);
                    List<Integer> controlledChars = contAttrDesc.readControlledChars();
                    contChars.addAll(controlledChars);
                }
                if (contChars.contains(charBase.getUniId()) || contChars.contains(baseId)) {
                    throw new CircularDependencyException();
                }
                if (includeIndirect) {
                    // OK. We now have a list of all characters DIRECTLY
                    // controlled
                    // by this one. We should add all those INDIRECTLY
                    // controlled as well.
                    List<Integer> contDirect = new ArrayList<Integer>(contChars);

                    for (Integer i : contDirect) {
                        List<Integer> contIndirect = new ArrayList<Integer>();
                        VOCharBaseDesc indirCharBase = getDescFromId(i);
                        if (getControlledChars(testedControlling, indirCharBase, contIndirect, true, baseId)) {
                            if (contIndirect.contains(baseId)) {
                                throw new CircularDependencyException();
                            }
                            contChars.addAll(contIndirect);
                        }
                    }
                }
            }
            return !contChars.isEmpty();
        }
    }

    @Override
    public void addDependentCharacters(CharacterDependency dependency) {
        synchronized (_vop) {
            VOControllingAdapter impl = (VOControllingAdapter) dependency.getImpl();
            _charDesc.addControllingInfo(impl.getId());
        }

    }

    @Override
    public List<CharacterDependency> getDependentCharacters() {
        synchronized (_vop) {
            List<Integer> controllingInfoIds = _charDesc.readDependentContAttrs();
            return idListToCharacterDependencyList(controllingInfoIds);
        }
    }

    /**
     * Creates and returns a CharacterDependency populated from the supplied
     * VOControllingDesc.
     * 
     * @param controllingDesc
     *            the source of data for the CharacterDependency.
     * @return a new CharacterDependency.
     */
    private CharacterDependency characterDependencyFromControllingInfo(VOControllingDesc controllingDesc) {
        return new CharacterDependency(new VOControllingAdapter(getVOP(), controllingDesc));
    }

    @Override
    public void addControllingCharacters(CharacterDependency dependency) {
        synchronized (_vop) {
            int id = VOControllingAdapter.getId(dependency);
            _charDesc.addControllingInfo(id);
        }

    }

    @Override
    public void removeControllingCharacter(CharacterDependency dependency) {
        synchronized (_vop) {
            int id = VOControllingAdapter.getId(dependency);
            _charDesc.removeControllingInfo(id);
        }

    }

    @Override
    public List<CharacterDependency> getControllingCharacters() {
        synchronized (_vop) {
            List<Integer> controllingInfoIds = _charDesc.readControllingInfo();
            return idListToCharacterDependencyList(controllingInfoIds);
        }

    }

    private List<CharacterDependency> idListToCharacterDependencyList(List<Integer> controllingInfoIds) {
        List<CharacterDependency> charDependencyList = new ArrayList<CharacterDependency>();

        for (int id : controllingInfoIds) {
            VOControllingDesc controllingDesc = (VOControllingDesc) getDescFromId(id);

            CharacterDependency dependency = characterDependencyFromControllingInfo(controllingDesc);
            charDependencyList.add(dependency);
        }
        return charDependencyList;
    }

    @Override
    public void addState(int stateNumber) {
        synchronized (_vop) {
            _charDesc.insertState(stateNumber, getVOP());
        }
    }

    @Override
    public void moveState(int stateNumber, int newNumber) {
        synchronized (_vop) {
            _charDesc.moveState(stateNumber, newNumber);
        }
    }

    /**
     * Changes the character type. If the Character is a multistate Character,
     * all of the states will be deleted before changing the type.
     * 
     * @param newType
     *            the new type for this character.
     */
    public void setCharacterType(CharacterType newType) {
        synchronized (_vop) {
            if (CharType.isMultistate(_charDesc.getCharType()) && (!newType.isMultistate())) {
                _charDesc.setCodedImplicit(VOCharBaseDesc.STATEID_NULL);
                _charDesc.setUncodedImplicit(VOCharBaseDesc.STATEID_NULL);
            }

            _charDesc.setCharType(CharacterTypeConverter.toCharType(newType));
        }
    }

    @Override
    public float getReliability() {
        throw new NotImplementedException();
    }

    @Override
    public void setReliability(float reliability) {
        throw new NotImplementedException();
    }

    @Override
    public int getMaximumValue() {
        // Always return 0, maximum value for a character
        // is not stored in slot file
        return 0;
    }

    @Override
    public void setMaximumValue(int max) {
        // Do nothing, maximum value for a character is not stored in slot file.
    }

    @Override
    public int getMinimumValue() {
        // Always return 0, minimum value for a character
        // is not stored in slot file
        return 0;
    }

    @Override
    public void setMinimumValue(int min) {
        // Do nothing, maximum value for a character is not stored in slot file.
    }

    @Override
    public String getItemSubheading() {
        throw new NotImplementedException();
    }

    @Override
    public void setItemSubheading(String charItemSubheading) {
        throw new NotImplementedException();
    }

    @Override
    public List<Float> getKeyStateBoundaries() {
        throw new NotImplementedException();
    }

    @Override
    public void setKeyStateBoundaries(List<Float> keyStateBoundaries) {
        throw new NotImplementedException();
    }

    @Override
    public boolean getContainsSynonmyInformation() {
        throw new NotImplementedException();
    }

    @Override
    public void setContainsSynonmyInformation(boolean containsSynonmyInfo) {
        throw new NotImplementedException();
    }

    @Override
    public boolean getOmitOr() {
        throw new NotImplementedException();
    }

    @Override
    public void setOmitOr(boolean omitOr) {
        throw new NotImplementedException();
    }

    @Override
    public boolean getUseCc() {
        throw new NotImplementedException();
    }

    @Override
    public void setUseCc(boolean useCc) {
        throw new NotImplementedException();
    }

    @Override
    public boolean getOmitPeriod() {
        throw new NotImplementedException();
    }

    @Override
    public void setOmitPeriod(boolean omitPeriod) {
        throw new NotImplementedException();
    }

    @Override
    public boolean getNewParagraph() {
        throw new NotImplementedException();
    }

    @Override
    public void setNewParagraph(boolean newParagraph) {
        throw new NotImplementedException();
    }

    @Override
    public boolean getNonAutoCc() {
        throw new NotImplementedException();
    }

    @Override
    public void setNonAutoCc(boolean nonAutoCc) {
        throw new NotImplementedException();
    }

    @Override
    protected DeltaVOP getVOP() {
        return _vop;
    }

    @Override
    protected VOImageHolderDesc getImageHolder() {
        return _charDesc;
    }

    @Override
    public boolean isIntegerRepresentedAsReal() {
        throw new NotImplementedException();
    }

    @Override
    public void setIntegerRepresentedAsReal(boolean isIntegerRepresentedAsReal) {
        throw new NotImplementedException();
    }
}
