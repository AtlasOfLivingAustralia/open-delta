package au.org.ala.delta.rtf;

import java.util.HashMap;
import java.util.Map;

public class CharacterAttributes {
		
	private Map<CharacterAttributeType, Integer> _attrMap = new HashMap<CharacterAttributeType, Integer>();
	
	public CharacterAttributes() {
		for (CharacterAttributeType attrType : CharacterAttributeType.values()) {
			_attrMap.put(attrType, 0);
		}
	}
	
	public CharacterAttributes(CharacterAttributes other) {
		for (CharacterAttributeType attrType : CharacterAttributeType.values()) {
			_attrMap.put(attrType, other.get(attrType));
			
		}
	}
	
	public int get(CharacterAttributeType attrType) {
		return _attrMap.get(attrType);		
	}
	
	public void set(CharacterAttributeType attrType, int value) {
		_attrMap.put(attrType, value);
	}

}
