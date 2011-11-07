package au.org.ala.delta.intkey.ui;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
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
    private CharacterFormatter _charFormatter;
    private AttributeFormatter _attrFormatter;

    public AttributeCellRenderer(boolean displayNumbering, String orWord) {
        _charactersToColor = new HashSet<Character>();
        _attrFormatter = new AttributeFormatter(displayNumbering, true, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, orWord);
        _charFormatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false);
    }

    @Override
    protected String getTextForValue(Object value) {
        Attribute attr = (Attribute) value;
        
        return String.format("%s %s", _charFormatter.formatCharacterDescription(attr.getCharacter()), _attrFormatter.formatAttribute((Attribute) value));
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
