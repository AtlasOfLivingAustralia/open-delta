package au.org.ala.delta.translation.parameter;

public class StateEncoder {

	public static final String[] STATE_CODES = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
		"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
		"U", "V", "W", "X", "Y", "Z"};
	
	private boolean _numberFromZero;
	
	public StateEncoder(boolean numberFromZero) {
		_numberFromZero = numberFromZero;
	}
	
	public String encodeState(int stateNum) {
		if (_numberFromZero) {
			stateNum--;
		}
		return STATE_CODES[stateNum];
	}
}
