package au.org.ala.delta.rtf;

public class ParagraphAttributes {
	
	public int LeftIndent = 0;
	public int RightIndent = 0;
	public int FirstLineIndent = 0;
	JustificationType Justification = JustificationType.Left;
	
	public ParagraphAttributes() {		
	}
	
	public ParagraphAttributes(ParagraphAttributes other) {
		LeftIndent = other.LeftIndent;
		RightIndent = other.RightIndent;
		FirstLineIndent = other.FirstLineIndent;
		Justification = other.Justification;
	}
	
}
