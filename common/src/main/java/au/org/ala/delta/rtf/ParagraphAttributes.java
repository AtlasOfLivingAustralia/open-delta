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
