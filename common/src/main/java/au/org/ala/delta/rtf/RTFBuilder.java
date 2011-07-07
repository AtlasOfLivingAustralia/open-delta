package au.org.ala.delta.rtf;

public class RTFBuilder {

    private StringBuilder _strBuilder;

    // by default, the indent width is 720, which is the default tab size.
    private int _indentWidth = 720;
    private int _currentIndent = 0;

    public RTFBuilder() {
        _strBuilder = new StringBuilder();
    }

    public String toString() {
        return _strBuilder.toString();
    }

    public void setIdentWidth(int twips) {
        _indentWidth = twips;
    }

    public void startDocument() {
        _strBuilder.append("{\\rtf1\\ansi\\deff0 {\fonttbl {\f0 Times New Roman;}}");
        _strBuilder.append("{\\colortbl ;\\red255\\green0\\blue0;}");
    }

    public void endDocument() {
        _strBuilder.append("}");
    }

    public void startParagraph() {
        _strBuilder.append("\\pard");
    }

    public void endParagraph() {
        _strBuilder.append("\\par");
    }

    public void increaseIndent() {
        _currentIndent++;
        int indentTwips = _currentIndent * _indentWidth;
        _strBuilder.append(String.format("\\li%s", indentTwips));
    }

    public void decreaseIndent() {
        _currentIndent--;
        int indentTwips = _currentIndent * _indentWidth;
        _strBuilder.append(String.format("\\li%s", indentTwips));
    }
    
    public void appendText(String str) {
        _strBuilder.append(str);
    }

}
