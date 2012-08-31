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
package au.org.ala.delta.intkey.ui;

import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.model.Character;

public class BestCharacterCellRenderer extends CharacterCellRenderer {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4185197144357517831L;

    private Map<Character, Double> _separatingPowers;
    
    public BestCharacterCellRenderer(Map<Character, Double> separatingPowers, boolean displayNumbering) {
        super(displayNumbering);
        _separatingPowers = new HashMap<Character, Double>(separatingPowers);
    }

    @Override
    protected String getTextForValue(Object value) {
        if (value instanceof Character) {
            Character ch = (Character) value;
            double separatingPower = _separatingPowers.get(ch);
            String charDescription = _formatter.formatCharacterDescription(ch);
            return String.format("%.2f    %s", separatingPower, charDescription);
        } else {
            return value.toString();
        }
    }

}
