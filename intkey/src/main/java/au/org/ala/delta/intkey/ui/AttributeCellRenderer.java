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

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

public class AttributeCellRenderer extends ColoringListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -8919505858741276673L;

    protected Set<Character> _charactersToColor;
    private CharacterFormatter _charFormatter;
    private AttributeFormatter _attrFormatter;

    public AttributeCellRenderer(boolean displayNumbering, String orWord) {
        _charactersToColor = new HashSet<Character>();
        _attrFormatter = new AttributeFormatter(displayNumbering, true, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, orWord);
        _charFormatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, true, false);
    }

    @Override
    protected String getTextForValue(Object value) {
        Attribute attr = (Attribute) value;
        
        String str = String.format("%s %s", _charFormatter.formatCharacterDescription(attr.getCharacter()), _attrFormatter.formatAttribute((Attribute) value)); 
        return str;
    }

    @Override
    protected boolean isValueColored(Object value) {
        if (value instanceof Attribute) {
            Attribute attr = (Attribute) value;
            return _charactersToColor.contains(attr.getCharacter());
        } else {
            return false;
        }
    }

    public void setCharactersToColor(Set<Character> charactersToColor) {
        _charactersToColor = new HashSet<Character>(charactersToColor);
    }
}
