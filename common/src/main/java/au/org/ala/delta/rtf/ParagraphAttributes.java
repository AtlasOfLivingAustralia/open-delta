package au.org.ala.delta.rtf;

import java.util.HashMap;
import java.util.Map;

public class ParagraphAttributes {
	
    private Map<ParagraphAttributeType, Integer> _attrMap = new HashMap<ParagraphAttributeType, Integer>();
    
    public ParagraphAttributes() {
        for (ParagraphAttributeType attrType : ParagraphAttributeType.values()) {
            _attrMap.put(attrType, 0);
        }
    }
    
    public ParagraphAttributes(ParagraphAttributes other) {
        for (ParagraphAttributeType attrType : ParagraphAttributeType.values()) {
            _attrMap.put(attrType, other.get(attrType));
            
        }
    }
    
    public int get(ParagraphAttributeType attrType) {
        return _attrMap.get(attrType);      
    }
    
    public void set(ParagraphAttributeType attrType, int value) {
        _attrMap.put(attrType, value);
    }
	
}
