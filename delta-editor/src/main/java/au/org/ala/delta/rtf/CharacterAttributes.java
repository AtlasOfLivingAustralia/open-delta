package au.org.ala.delta.rtf;

public class CharacterAttributes {
	
	public boolean Bold;
	public boolean Underline;
	public boolean Italic;
	public boolean Subscript;
	public boolean Superscript;
	
	public CharacterAttributes() {
		Bold = Underline = Italic = Subscript = Superscript = false;
	}
	
	public CharacterAttributes(CharacterAttributes other) {
		Bold = other.Bold;
		Underline = other.Underline;
		Italic = other.Italic;
		Subscript = other.Subscript;
		Superscript = other.Subscript;
	}

}
