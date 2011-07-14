package au.org.ala.delta.rtf;

public enum CharacterAttributeType {
	Bold("b"),
	Underline("ul"),
	Italics("i"),
	Superscript("super"),
	Subscript("sub"),
	Font("f"),
	FontSize("fs"),
	FontColor("cf"),
	NoSuperscriptOrSubscript("nosupersub");
	
	private String _keyword;
	private CharacterAttributeType(String keyword) {
		_keyword = keyword;
	}
	
	public String keyword() {
		return _keyword;
	}
}
