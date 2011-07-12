package au.org.ala.delta.rtf;

public enum ParagraphAttributeType {
    LeftJustify("ql"),
    RightJustify("qr"),
    FullJustify("qj"),
    CenterJustify("qc"),
    FirstLineIndent("fi"),
    LeftBlockIndent("li"),
    RightBlockIndent("ri");
    
    private String _keyword;
    private ParagraphAttributeType(String keyword) {
        _keyword = keyword;
    }
    
    public String keyword() {
        return _keyword;
    }
}
