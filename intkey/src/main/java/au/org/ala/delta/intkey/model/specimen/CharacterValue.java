package au.org.ala.delta.intkey.model.specimen;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

public abstract class CharacterValue {

    CharacterFormatter _formatter;
    
    public CharacterValue() {
        _formatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false);
    }
    
    public abstract Character getCharacter();
    
    public abstract String toShortString();
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(Object obj);
}
