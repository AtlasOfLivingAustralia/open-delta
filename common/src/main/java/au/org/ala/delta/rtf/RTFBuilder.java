package au.org.ala.delta.rtf;

import java.awt.Color;

public class RTFBuilder {

    private StringBuilder _strBuilder;

    // by default, the indent width is 720, which is the default tab size.
    private int _indentWidth = 720;
    private int _currentIndent = 0;

    public static enum Alignment {
        LEFT, RIGHT, CENTER, JUSTIFY
    }

    private Alignment _alignment = Alignment.LEFT;

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
        _strBuilder.append("{\\rtf1\\ansi\\deff0 {\\fonttbl {\\f0\\froman;}{\\f1\\fswiss;}}");
        _strBuilder.append("\n");
        _strBuilder.append("{\\colortbl ;\\red255\\green0\\blue0;}");
        _strBuilder.append("\n");
        _strBuilder.append("\\fs36");
        _strBuilder.append("\n");
    }

    public void endDocument() {
        _strBuilder.append("}");
        _strBuilder.append("\n");
    }

    public void increaseIndent() {
        _currentIndent++;
    }

    public void decreaseIndent() {
        _currentIndent--;
    }

    public void setAlignment(Alignment alignment) {
        _alignment = alignment;
    }

    public void setTextColor(Color color) {
        if (color.equals(Color.BLACK)) {
            _strBuilder.append("\\cf0");
        } else if (color.equals(Color.RED)) {
            _strBuilder.append("\\cf1");
        } else {
            throw new IllegalArgumentException("Unsupported color");
        }
    }

    public void setFont(int fontNumber) {
        if (fontNumber == 0) {
            _strBuilder.append("\\f0");
        } else if (fontNumber == 1) {
            _strBuilder.append("\\f1");
        } else {
            throw new IllegalArgumentException("Unrecognised font number");
        }
    }

    public void appendText(String str) {
        _strBuilder.append("\\pard ");

        if (_alignment != null) {
            switch (_alignment) {
            case LEFT:
                _strBuilder.append("\\ql ");
                break;
            case RIGHT:
                _strBuilder.append("\\qr ");
                break;
            case CENTER:
                _strBuilder.append("\\qc ");
                break;
            case JUSTIFY:
                _strBuilder.append("\\qj ");
                break;
            }
        }

        int indentTwips = _currentIndent * _indentWidth;
        _strBuilder.append(String.format("\\li%s ", indentTwips));
        _strBuilder.append(str);
        _strBuilder.append("\\par ");
        _strBuilder.append("\n");
    }

}
