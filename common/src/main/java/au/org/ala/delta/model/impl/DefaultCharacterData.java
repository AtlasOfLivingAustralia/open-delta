package au.org.ala.delta.model.impl;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.model.Item;

/**
 * An implementation of CharacterData that maintains the data in-memory.
 */
public class DefaultCharacterData implements CharacterData {

	private String _notes;
	private String _description;
	private boolean _exclusive;
	private boolean _mandatory;
	private String _units;
	private String[] _states = new String[0];
	private int _codedImplicitStateId;
	private int _uncodedImplicitStateId;

	@Override
	public String getDescription() {
		return _description;
	}
	
	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public boolean isExclusive() {
		return _exclusive;
	}

	@Override
	public boolean isMandatory() {
		return _mandatory;
	}

	@Override
	public String getUnits() {
		return _units;
	}
	
	@Override
	public void setUnits(String units) {
		_units = units;
	}

	@Override
	public String getStateText(int stateNumber) {
		return _states[stateNumber-1];
	}
	
	@Override
	public void setStateText(int stateNumber, String text) {
		
		_states[stateNumber-1] = text;
	}
	
	@Override
	public void setNumberOfStates(int numStates) {
		_states = new String[numStates];
		
	}

	@Override
	public int getNumberOfStates() {
		return _states.length;
	}

	@Override
	public void setMandatory(boolean mandatory) {
		_mandatory = mandatory;
	}

	/**
	 * @return the notes about this character
	 */
	public String getNotes() {
		return _notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		_notes = notes;
	}

	
	@Override
	public void setExclusive(boolean exclusive) {
		_exclusive = exclusive;
		
	}

	@Override
	public int getCodedImplicitState() {
		return _codedImplicitStateId;
	}

	@Override
	public void setCodedImplicitState(int stateId) {
		_codedImplicitStateId = stateId;
	}

	@Override
	public int getUncodedImplicitState() {
		return _uncodedImplicitStateId;
	}

	@Override
	public void setUncodedImplicitState(int stateId) {
		_uncodedImplicitStateId = stateId;
	}

	@Override
	public void validateAttributeText(String text) {
		throw new NotImplementedException();
	}

	@Override
	public ControllingInfo checkApplicability(Item item) {
		throw new NotImplementedException();
	}
	
}
