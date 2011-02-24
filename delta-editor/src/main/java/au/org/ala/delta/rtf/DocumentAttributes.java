package au.org.ala.delta.rtf;

public class DocumentAttributes {
	
	public int PageWidth;
	public int PageHeight;
	public int LeftMargin;
	public int TopMargin;
	public int RightMargin;
	public int BottomMargin;
	public int StartingPageNumber;
	public boolean FacingPages;
	public boolean Landscape;
	
	public DocumentAttributes() {		
	}
	
	public DocumentAttributes(DocumentAttributes other) {
		PageWidth = other.PageWidth;
		PageHeight = other.PageHeight;
		LeftMargin = other.LeftMargin;
		TopMargin = other.TopMargin;
		RightMargin = other.RightMargin;
		BottomMargin = other.BottomMargin;
		StartingPageNumber = other.StartingPageNumber;
		FacingPages = other.FacingPages;
		Landscape = other.Landscape;		
	}
}
