package au.org.ala.delta.intkey.ui;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;

public class CharacterCellRenderer extends ColoringListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 2386540902414786423L;
    
    protected Set<Character> _charactersToColor;
    protected CharacterFormatter _formatter;

    public CharacterCellRenderer() {
        _charactersToColor = new HashSet<Character>();
        _formatter = new CharacterFormatter(false, false, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true);
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
