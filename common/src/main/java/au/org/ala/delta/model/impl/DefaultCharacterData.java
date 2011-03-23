package au.org.ala.delta.model.impl;

/**
 * An implementation of CharacterData that maintains the data in-memory.
 */
public class DefaultCharacterData implements CharacterData {

	private String _description;
	private boolean _exclusive;
	private boolean _mandatory;
	private String _units;
	private String[] _states = new String[0];

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

}
