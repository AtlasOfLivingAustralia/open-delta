package au.org.ala.delta.rtf;

public class ParserState {
	
	public CharacterAttributes CharacterAttributes;
	public ParagraphAttributes ParagraphAttributes;
	public SectionAttributes SectionAttributes;
	public DocumentAttributes DocumentAttributes;
	public DestinationState rds;
	public ParserInternalState ris;
	
	public ParserState() {
		CharacterAttributes = new CharacterAttributes();
		ParagraphAttributes = new ParagraphAttributes();
		SectionAttributes = new SectionAttributes();
		DocumentAttributes = new DocumentAttributes();
		rds = DestinationState.Normal;
		ris = ParserInternalState.Normal;
	}
	
	public ParserState(ParserState other) {
		rds = other.rds;
		ris = other.ris;
		CharacterAttributes = new CharacterAttributes(other.CharacterAttributes);
		ParagraphAttributes = new ParagraphAttributes(other.ParagraphAttributes);
		SectionAttributes = new SectionAttributes(other.SectionAttributes);
		DocumentAttributes = new DocumentAttributes(other.DocumentAttributes);
	}
	
}
