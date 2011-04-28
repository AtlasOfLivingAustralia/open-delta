package au.org.ala.delta.model.impl;


/**
 * A simple implementation of AttributeData that stores attribute data in-memory.
 */
public class DefaultAttributeData implements AttributeData {

	private String _value;
	
	@Override
	public String getValue() {
		return _value;
	}

	@Override
	public void setValue(String value) {
		_value = value;
	}

	@Override
	public boolean isStatePresent(int stateNumber) {
		return false;
		
	}
	
	public void setStatePresent(int stateNumber, boolean present) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSimple() {
		return false;
	}

}
