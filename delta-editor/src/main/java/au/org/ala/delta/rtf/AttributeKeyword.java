package au.org.ala.delta.rtf;

public class AttributeKeyword extends Keyword {
	
	private int _default;
	private boolean _useDefault;
	private CharacterAttributeType _type;

	public AttributeKeyword(String keyword, CharacterAttributeType type, int defval, boolean useDefault) {
		super(keyword, KeywordType.Attribute);
		_default = defval;
		_useDefault = useDefault;
		_type = type;
	}
	
	public int getDefaultValue() {
		return _default;
	}
	
	public boolean useDefault() {
		return _useDefault;
	}
	
	public CharacterAttributeType getAttributeType() {
		return _type;
	}

}
