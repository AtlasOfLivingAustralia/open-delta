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
package au.org.ala.delta.key;

import java.util.HashMap;
import java.util.Map;

public class ConfirmatoryCharacter {

    private int _characterNumber;
    private int _mainCharacterNumber;
    private Map<Integer, Integer> _mainToConfirmatoryStateMap;

    public ConfirmatoryCharacter(int characterNumber, int mainCharacterNumber, Map<Integer, Integer> mainToConfirmatoryStateMap) {
        _characterNumber = characterNumber;
        _mainCharacterNumber = mainCharacterNumber;
        _mainToConfirmatoryStateMap = new HashMap<Integer, Integer>(mainToConfirmatoryStateMap);
    }

    public int getConfirmatoryCharacterNumber() {
        return _characterNumber;
    }

    public int getMainCharacterNumber() {
        return _mainCharacterNumber;
    }

    public int getConfirmatoryStateNumber(int mainCharacterState) {
        if (_mainToConfirmatoryStateMap.containsKey(mainCharacterState)) {
            return _mainToConfirmatoryStateMap.get(mainCharacterState);
        } else {
            return -1;
        }
    }
}
