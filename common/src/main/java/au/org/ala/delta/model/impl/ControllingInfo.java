package au.org.ala.delta.model.impl;

public class ControllingInfo {
	
	public enum ControlledStateType {
		NotControlled, Inapplicable, InapplicableOrUnknown, MaybeInapplicable
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
	
	/**
	 * A Character is "maybe inapplicable" if the controlling attribute takes
	 * one of the values that render the Character inapplicable, but also 
	 * another value.
	 * For example, given the directive DEPENDENT CHARACTERS 10,2:11
	 * if for a given item 10,2 would render character 11 inapplicable
	 * but 10,1/2 would render character 11 maybe inapplicable.  (This
	 * distinction is used by CONFOR when translating into intkey and nexus 
	 * format. 
	 * @return true if the result of the applicability check was maybe inapplicable.
	 */
	public boolean isMaybeInapplicable() {
		return _state == ControlledStateType.MaybeInapplicable;
	}

}
