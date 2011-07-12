package au.org.ala.delta.rtf;

public class CharacterAttributeKeyword extends AttributeKeyword {

    private CharacterAttributeType _type;
    
    public CharacterAttributeKeyword(String keyword, CharacterAttributeType type, int defval, boolean useDefault) {
        super(keyword, defval, useDefault);
        _type = type;
    }
    
    public CharacterAttributeType getType() {
        return _type;
    }
}
