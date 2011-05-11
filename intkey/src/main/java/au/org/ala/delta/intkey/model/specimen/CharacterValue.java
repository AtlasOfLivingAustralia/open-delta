package au.org.ala.delta.intkey.model.specimen;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;

public abstract class CharacterValue {

    CharacterFormatter _formatter;
    
    public CharacterValue() {
        _formatter = new CharacterFormatter(false, true, true, true);
    }
    
    public abstract Character getCharacter();
}
