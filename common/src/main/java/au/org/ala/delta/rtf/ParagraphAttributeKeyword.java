package au.org.ala.delta.rtf;

public class ParagraphAttributeKeyword extends AttributeKeyword {

    private ParagraphAttributeType _type;
    
    public ParagraphAttributeKeyword(String keyword, ParagraphAttributeType type, int defval, boolean useDefault) {
        super(keyword, defval, useDefault);
        _type = type;
    }
    
    public ParagraphAttributeType getType() {
        return _type;
    }
}
