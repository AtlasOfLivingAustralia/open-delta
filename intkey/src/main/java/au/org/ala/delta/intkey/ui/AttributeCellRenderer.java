package au.org.ala.delta.intkey.ui;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.intkey.model.specimen.SpecimenValue;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

//TODO this class currently uses CharacterValue objects, but needs to be 
//refactored to use Attribute objects
public class AttributeCellRenderer extends ColoringListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -8919505858741276673L;

    protected Set<Character> _charactersToColor;
    private AttributeFormatter _formatter;

    public AttributeCellRenderer(boolean displayNumbering, String orWord) {
        _charactersToColor = new HashSet<Character>();
        _formatter = new AttributeFormatter(displayNumbering, true, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, orWord);
    }

    @Override
    protected String getTextForValue(Object value) {
        return value.toString();
    }

    @Override
    protected boolean isValueColored(Object value) {
        if (value instanceof SpecimenValue) {
            SpecimenValue charVal = (SpecimenValue) value;
            return _charactersToColor.contains(charVal.getCharacter());
        } else {
            return false;
        }
    }

    public void setCharactersToColor(Set<Character> charactersToColor) {
        _charactersToColor = new HashSet<Character>(charactersToColor);
    }
}
