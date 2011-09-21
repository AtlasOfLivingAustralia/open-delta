package au.org.ala.delta.intkey.model.specimen;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;

public abstract class CharacterValue {

    CharacterFormatter _formatter;
    
    public CharacterValue() {
        _formatter = new CharacterFormatter(false, true, AngleBracketHandlingMode.REMOVE, true);
    }
    
    public abstract Character getCharacter();
    
    public abstract String toShortString();
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(Object obj);
}
