package au.org.ala.delta.rtf;

public class AttributeKeyword extends Keyword {
	
	private int _default;
	private boolean _useDefault;


	public AttributeKeyword(String keyword, int defval, boolean useDefault) {
		super(keyword, KeywordType.Attribute);
		_default = defval;
		_useDefault = useDefault;
	}
	
	public int getDefaultValue() {
		return _default;
	}
	
	public boolean useDefault() {
		return _useDefault;
	}

}
