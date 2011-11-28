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

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

public class CharacterCellRenderer extends ColoringListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 2386540902414786423L;
    
    protected Set<Character> _charactersToColor;
    protected CharacterFormatter _formatter;

    public CharacterCellRenderer(boolean displayNumbering) {
        _charactersToColor = new HashSet<Character>();
        _formatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true, false);
    }

    @Override
    protected boolean isValueColored(Object value) {
        if (value instanceof Character) {
            return _charactersToColor.contains(value);
        } else {
            return false;
        }
    }

    @Override
    protected String getTextForValue(Object value) {
        if (value instanceof Character) {
            Character ch = (Character) value;
            String charDescription = _formatter.formatCharacterDescription(ch);
            return charDescription;
        } else {
            return value.toString();
        }
    }
    
    public void setCharactersToColor(Set<Character> charactersToColor) {
        _charactersToColor = new HashSet<Character>(charactersToColor);
    }

}
