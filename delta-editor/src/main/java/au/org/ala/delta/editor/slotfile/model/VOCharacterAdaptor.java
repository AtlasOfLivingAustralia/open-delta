package au.org.ala.delta.editor.slotfile.model;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOCharTextDesc;
import au.org.ala.delta.model.impl.CharacterData;

/**
 * Adapts the CharacterData interface to the VOCharBaseDesc and VOCharTextDesc slot file
 * classes.
 */
public class VOCharacterAdaptor implements CharacterData {
	
	/** If they've been specified, units are stored as state text for state number 1. */
	private static final int UNITS_TEXT_STATE_NUMBER = 1;
	
	private VOCharBaseDesc _charDesc;
	private VOCharTextDesc _textDesc;
	
	public VOCharacterAdaptor(VOCharBaseDesc charBase) {
		this (charBase, null);
	}
	
	public VOCharacterAdaptor(VOCharBaseDesc charBase, VOCharTextDesc textDesc) {
		_charDesc = charBase;
		_textDesc = textDesc;
	}
	
	@Override
	public String getDescription() {
		String description = "";
		if (_textDesc != null) {
			description = _textDesc.readFeatureText(TextType.RTF);
		}
		return description;
	}
	
	@Override
	public void setDescription(String desc) {
		_textDesc.makeTemp();
		_textDesc.writeFeatureText(desc);
	}

	@Override
	public void setUnits(String units) {
		if (_charDesc.getNStatesUsed() > 0) {
			throw new NotImplementedException("Deleting existing states not implemented!");
		}
		_charDesc.setInitialStateNumber(1);
		int stateId = _charDesc.uniIdFromStateNo(1);
		_textDesc.makeTemp();
		_textDesc.writeStateText(units, stateId);
	}

	@Override
	public boolean isExclusive() {
		return _charDesc.testCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE);
	}
	
	@Override
	public void setExclusive(boolean b) {
		_charDesc.setCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE);
	}

	@Override
	public boolean isMandatory() {
		return _charDesc.testCharFlag(VOCharBaseDesc.CHAR_MANDATORY);
	}
	
	@Override
	public void setMandatory(boolean b) {
		_charDesc.setCharFlag(VOCharBaseDesc.CHAR_MANDATORY);
	}


	public VOCharBaseDesc getCharBaseDesc() {
		return _charDesc;
	}

	@Override
	public String getUnits() {
		String units = "";
		if (_charDesc.getNStatesUsed() >= UNITS_TEXT_STATE_NUMBER) {
			units = getStateText(UNITS_TEXT_STATE_NUMBER);
		}
		return units;
	}
	
	@Override
	public String getStateText(int stateNumber) {
		if (_textDesc == null) {
			return "";
		}
		int stateId = _charDesc.uniIdFromStateNo(stateNumber);
		return _textDesc.readStateText(stateId, TextType.UTF8);
	}


	@Override
	public int getNumberOfStates() {
		// Trying to read past the number of states actually used yields an error
		return _charDesc.getNStatesUsed();
	}
	
	

	@Override
	public void setStateText(int stateNumber, String text) {
		int stateId = _charDesc.uniIdFromStateNo(stateNumber);
		_textDesc.writeStateText(text, stateId);
	}

	@Override
	public void setNumberOfStates(int numStates) {
		
		if (_charDesc.getNStatesUsed() > 0) {
			throw new NotImplementedException("Ooops, don't currently handle deleting existing states...");
		}
		_charDesc.setInitialStateNumber(numStates);
	}

	@Override
	public String getNotes() {
		return _textDesc.readNoteText(TextType.RTF); 	
	}	
	
	@Override
	public void setNotes(String note) {
		_textDesc.writeNoteText(note);
	}

	@Override
	public int getCodedImplicitState() {
		return _charDesc.stateNoFromUniId(_charDesc.getCodedImplicit());
	}

	@Override
	public int getUncodedImplicitState() {	
		return _charDesc.stateNoFromUniId(_charDesc.getUncodedImplicit());
	}

	@Override
	public void setCodedImplicitState(int stateNo) {
		int stateId = _charDesc.uniIdFromStateNo(stateNo);
		_charDesc.setCodedImplicit((short) stateId);
		
	}

	@Override
	public void setUncodedImplicitState(int stateNo) {
		int stateId = _charDesc.uniIdFromStateNo(stateNo);
		_charDesc.setUncodedImplicit((short) stateId);
	}

	@Override
	public void validateAttributeText(String text) {
		new Attribute(text, _charDesc);
	}	
}
