package au.org.ala.delta.rtf;

public class SectionAttributes {
	public int columns = 1;
	public SectionBreakType sectionBreak = SectionBreakType.NonBreaking;
	public int xPgn;
	public int yPgn;
	public PageNumberFormatType pageNumberFormat;
	
	public SectionAttributes() {	
	}
	
	public SectionAttributes(SectionAttributes other) {
		columns = other.columns;
		sectionBreak = other.sectionBreak;
		xPgn = other.xPgn;
		yPgn = other.yPgn;
		pageNumberFormat = other.pageNumberFormat;
	}
	
	
}
