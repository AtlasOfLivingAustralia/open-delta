package au.org.ala.delta.model.impl;

public class ControllingInfo {
	
	public enum ControlledStateType {
		NotControlled, Inapplicable, InapplicableOrUnknown
	}
	
	private ControlledStateType _state;
	private int _controllingCharacterNo;
	
	public ControllingInfo() {
		_state = ControlledStateType.NotControlled;
		_controllingCharacterNo = 0;		
	}
	
	public ControllingInfo(ControlledStateType state, int controllingCharNo) {
		_state = state;
		_controllingCharacterNo = controllingCharNo;
	}
	
	public ControlledStateType getControlledState() {
		return _state;
	}
	
	public int getControllingCharacter() {
		return _controllingCharacterNo;
	}
	
	public boolean isInapplicable() {
		return _state == ControlledStateType.Inapplicable || _state == ControlledStateType.InapplicableOrUnknown;
	}

}
