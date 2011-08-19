package au.org.ala.delta.intkey.ui;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.model.Character;

//TODO this class currently uses CharacterValue objects, but needs to be 
//refactored to use Attribute objects
public class AttributeCellRenderer extends ColoringListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -8919505858741276673L;
    
    protected Set<Character> _charactersToColor;
    
    public AttributeCellRenderer() {
        _charactersToColor = new HashSet<Character>();
    }
    
    @Override
    protected String getTextForValue(Object value) {
        return value.toString();
    }

    @Override
    protected boolean isValueColored(Object value) {
        if (value instanceof CharacterValue) {
            CharacterValue charVal = (CharacterValue) value;
            return _charactersToColor.contains(charVal.getCharacter());
        } else {
            return false;
        }
    }
    
    public void setCharactersToColor(Set<Character> charactersToColor) {
        _charactersToColor = new HashSet<Character>(charactersToColor);
    }
}
