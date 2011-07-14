package au.org.ala.delta.ui.rtf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.AttributeValue;
import au.org.ala.delta.rtf.CharacterAttributeType;
import au.org.ala.delta.rtf.ParagraphAttributeType;
import au.org.ala.delta.rtf.RTFHandlerAdapter;

/**
 * This class handles events from the RTFReader to build a StyledDocument
 * suitable for display by the StyledEditorKit.
 */
public class DocumentBuildingRtfHandler extends RTFHandlerAdapter {

    /** Buffers text until it is ready to be inserted into the Document */
    private StringBuilder _textBuffer;

    /**
     * The set of character attributes to be applied to the text in the
     * _textBuffer
     */
    private MutableAttributeSet _currentCharacterAttributes;

    private MutableAttributeSet _currentParagraphAttributes;

    /** The document we are building */
    private DefaultStyledDocument _document;

    /** Handlers for RTF attributes */
    private Map<String, AttributeHandler> _attributeHandlers = new HashMap<String, AttributeHandler>();

    private char _previousChar;

    private List<String> _fontFamilyNames = new ArrayList<String>();

    private List<Color> _colors = new ArrayList<Color>();

    private static Color _defaultColor = Color.BLACK;

    /**
     * Set by the "deffN" keyword
     */
    private int _defaultFont = 0;

    public void configureAttributeHandlers() {
        _attributeHandlers.put(CharacterAttributeType.Bold.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Bold));
        _attributeHandlers.put(CharacterAttributeType.Italics.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Italic));
        _attributeHandlers.put(CharacterAttributeType.Underline.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Underline));
        _attributeHandlers.put(CharacterAttributeType.Subscript.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Subscript));
        _attributeHandlers.put(CharacterAttributeType.Superscript.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Superscript));
        _attributeHandlers.put(CharacterAttributeType.NoSuperscriptOrSubscript.keyword(), new NoSuperscriptSubscriptHandler());
        _attributeHandlers.put(CharacterAttributeType.Font.keyword(), new FontAttributeHandler());
        _attributeHandlers.put(CharacterAttributeType.FontSize.keyword(), new FontSizeAttributeHandler());
        _attributeHandlers.put(CharacterAttributeType.FontColor.keyword(), new FontColorAttributeHandler());
        _attributeHandlers.put(ParagraphAttributeType.LeftJustify.keyword(), new ParagraphAlignmentAttributeHandler(StyleConstants.ALIGN_LEFT));
        _attributeHandlers.put(ParagraphAttributeType.RightJustify.keyword(), new ParagraphAlignmentAttributeHandler(StyleConstants.ALIGN_RIGHT));
        _attributeHandlers.put(ParagraphAttributeType.FullJustify.keyword(), new ParagraphAlignmentAttributeHandler(StyleConstants.ALIGN_JUSTIFIED));
        _attributeHandlers.put(ParagraphAttributeType.CenterJustify.keyword(), new ParagraphAlignmentAttributeHandler(StyleConstants.ALIGN_CENTER));
        _attributeHandlers.put(ParagraphAttributeType.FirstLineIndent.keyword(), new ParagraphIndentAttributeHandler(StyleConstants.FirstLineIndent));
        _attributeHandlers.put(ParagraphAttributeType.LeftBlockIndent.keyword(), new ParagraphIndentAttributeHandler(StyleConstants.LeftIndent));
        _attributeHandlers.put(ParagraphAttributeType.RightBlockIndent.keyword(), new ParagraphIndentAttributeHandler(StyleConstants.RightIndent));

    }

    /**
     * Knows how to convert an RTF character attribute into a StyledDocument
     * attribute.
     */
    public interface AttributeHandler {
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes);
    }

    /**
     * Handler for a simple boolean style attribute
     * 
     * @author ChrisF
     * 
     */
    public static class SimpleBooleanAttributeHandler implements AttributeHandler {
        private Object _styleAttribute;

        public SimpleBooleanAttributeHandler(Object styleAttribute) {
            _styleAttribute = styleAttribute;
        }

        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            newAttributes.addAttribute(_styleAttribute, Boolean.valueOf(!attr.hasParam()));
        }
    }
    
    /**
     * Handler for the "\nosupersub" attribute
     * @author ChrisF
     *
     */
    public static class NoSuperscriptSubscriptHandler implements AttributeHandler
    {

        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            newAttributes.addAttribute(StyleConstants.Subscript, false);
            newAttributes.addAttribute(StyleConstants.Superscript, false);
        }
        
    }
    /**
     * Handler for the font attribute "\fN"
     * 
     * @author ChrisF
     * 
     */
    public class FontAttributeHandler implements AttributeHandler {
        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            int fontIndex = attr.getParam();

            // ensure that the index is a valid index for a color specified in
            // the color table
            if (_fontFamilyNames.size() - 1 >= fontIndex) {
                newAttributes.addAttribute(StyleConstants.FontFamily, _fontFamilyNames.get(fontIndex));
            }
        }
    }

    /**
     * Handler for the font size attribute "\fsN"
     * 
     * @author ChrisF
     * 
     */
    public static class FontSizeAttributeHandler implements AttributeHandler {

        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            int fontSize = attr.getParam() / 2;
            newAttributes.addAttribute(StyleConstants.FontSize, fontSize);
        }

    }

    /**
     * Handler for the font foreground color attribute "\cfN"
     * 
     * @author ChrisF
     * 
     */
    public class FontColorAttributeHandler implements AttributeHandler {
        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            int colorIndex = attr.getParam();

            // ensure that the index is a valid index for a color specified in
            // the color table
            if (_colors.size() - 1 >= colorIndex) {
                newAttributes.addAttribute(StyleConstants.Foreground, _colors.get(colorIndex));
            }
        }
    }

    /**
     * Handler for the paragraph indent hander "\liN"
     * 
     * @author ChrisF
     * 
     */
    public static class ParagraphIndentAttributeHandler implements AttributeHandler {
        private Object _styleAttribute;

        public ParagraphIndentAttributeHandler(Object styleAttribute) {
            _styleAttribute = styleAttribute;
        }

        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            int indentInTwips = attr.getParam();
            float indentInPoints = indentInTwips / 20;
            newAttributes.addAttribute(_styleAttribute, indentInPoints);
        }
    }

    /**
     * Handler for the paragraph alignment attributes - "\ql", "\qr", "\qc" or
     * "\qj"
     * 
     * @author ChrisF
     * 
     */
    public static class ParagraphAlignmentAttributeHandler implements AttributeHandler {
        private int _alignmentAttribute;

        public ParagraphAlignmentAttributeHandler(int alignmentAttribute) {
            _alignmentAttribute = alignmentAttribute;
        }

        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            newAttributes.addAttribute(StyleConstants.Alignment, _alignmentAttribute);
        }
    }

    public DocumentBuildingRtfHandler(DefaultStyledDocument document) {
        configureAttributeHandlers();
        _currentCharacterAttributes = new SimpleAttributeSet();
        _currentParagraphAttributes = new SimpleAttributeSet();
        _document = document;
        _textBuffer = new StringBuilder();
    }

    @Override
    public void onKeyword(String keyword, boolean hasParam, int param) {
        // default font keyword
        if (keyword.equals("deff")) {
            _defaultFont = param;
        }
    }

    @Override
    public void onHeaderGroup(String keyword, String content) {
        if (keyword.equals("fonttbl")) {
            handleFontTable(content);
        } else if (keyword.equals("colortbl")) {
            handleColorTable(content);
        }
    }

    @Override
    public void onTextCharacter(char ch) {

        if (ch == 0) {
            return;
        }
        // Convert \r to \n as the editor pane ignores \r. Not sure what is
        // happening to the \n's... they don't seem to be coming through.
        if (ch != '\r') {
            if (_previousChar == '\r' && ch != '\n') {
                _textBuffer.append('\n');
            }
            _textBuffer.append(ch);
        }
        _previousChar = ch;
    }

    @Override
    public void endParse() {
        trimTrailingWhitespace();
        appendToDocument();
    }

    private void trimTrailingWhitespace() {

        int pos = _textBuffer.length() - 1;

        while ((pos >= 0) && Character.isWhitespace(_textBuffer.charAt(pos))) {
            _textBuffer.deleteCharAt(pos);
            pos--;
        }
    }

    @Override
    public void onCharacterAttributeChange(List<AttributeValue> values) {

        MutableAttributeSet newAttributes = new SimpleAttributeSet();
        newAttributes.addAttributes(_currentCharacterAttributes);

        handleAttributeChanges(values, newAttributes);

        if (!newAttributes.equals(_currentCharacterAttributes)) {
            appendToDocument();
            _currentCharacterAttributes = newAttributes;
        }

    }

    @Override
    public void onParagraphAttributeChange(List<AttributeValue> values) {
        MutableAttributeSet newAttributes = new SimpleAttributeSet();
        newAttributes.addAttributes(_currentParagraphAttributes);

        handleAttributeChanges(values, newAttributes);
        if (!newAttributes.equals(_currentParagraphAttributes)) {
            _currentParagraphAttributes = newAttributes;
        }
    }

    /**
     * Attempts to find a handler for each AttributeValue in the list.
     * 
     * @param values
     *            the AttributeValues that have changed.
     * @param newAttributes
     *            a container for any changes to the StyledDocument attributes
     *            that should be applied.
     */
    private void handleAttributeChanges(List<AttributeValue> values, MutableAttributeSet newAttributes) {

        for (AttributeValue attributeValue : values) {
            AttributeHandler handler = _attributeHandlers.get(attributeValue.getKeyword());
            if (handler != null) {
                handler.handleAttribute(attributeValue, newAttributes);
            }
        }
    }

    /**
     * Appends the current text buffer to the end of the Document we are
     * building with the set of current attributes.
     */
    private void appendToDocument() {
        appendToDocument(_textBuffer.toString());
    }

    /**
     * Appends the supplied text to the end of the Document we are building with
     * the set of current attributes.
     */
    private void appendToDocument(String text) {
        try {
            MutableAttributeSet copyAttributes = new SimpleAttributeSet();
            copyAttributes.addAttributes(_currentCharacterAttributes);

            // Use the default font if one has not been set
            if (copyAttributes.getAttribute(StyleConstants.FontFamily) == null) {
                copyAttributes.addAttribute(StyleConstants.FontFamily, _fontFamilyNames.get(_defaultFont));
            }

            // Use the default color if one has not been set
            if (copyAttributes.getAttribute(StyleConstants.Foreground) == null) {
                copyAttributes.addAttribute(StyleConstants.Foreground, _defaultColor);
            }

            _document.insertString(_document.getLength(), text, copyAttributes);
            _textBuffer = new StringBuilder();
        } catch (BadLocationException e) {
            throw new RuntimeException("Parsing the RTF document failed!", e);
        }
    }

    @Override
    public void endParagraph() {
        _document.setParagraphAttributes(_document.getLength(), 1, _currentParagraphAttributes, true);
        _textBuffer.append("\n");
        appendToDocument();
    }

    /**
     * Parse the content of the font table, indicated by the "fonttbl" keyword
     * 
     * @param content
     *            content of font table to be parsed
     */
    private void handleFontTable(String content) {

        String[] fontDefs = content.split("[;{}]");
        for (String fontDef : fontDefs) {
            if (fontDef.length() > 0) {
                int fontNumber = 0;
                StringBuilder fontNameBuilder = new StringBuilder();

                String[] tokens = fontDef.split(" ");
                for (String token : tokens) {
                    if (token.length() > 0) {
                        if (token.startsWith("\\")) {
                            if (token.matches("\\\\f\\d+")) {
                                try {
                                    fontNumber = Integer.parseInt(token.substring(2));
                                } catch (NumberFormatException ex) {
                                    // Parsing error due to incorrect RTF
                                    // source. Simply abort parsing the font
                                    // table.
                                    return;
                                }
                            }
                        } else {
                            fontNameBuilder.append(" ");
                            fontNameBuilder.append(token);
                        }
                    }
                }

                String fontName = fontNameBuilder.toString().trim();

                if (StringUtils.isBlank(fontName)) {
                    // Parsing error due to incorrect RTF source. Simply abort
                    // parsing the font table.
                    return;
                }

                _fontFamilyNames.add(fontNumber, fontName);
            }
        }
    }

    /**
     * Parse the content of the color table, indicated by the "colortbl" keyword
     * 
     * @param content
     *            content of color table to be parsed
     */
    private void handleColorTable(String content) {
        String[] colorDefs = content.split(";");

        for (String colorDef : colorDefs) {
            if (colorDef.length() > 0) {
                int red = 0;
                int green = 0;
                int blue = 0;

                String[] tokens = colorDef.split(" ");
                for (String token : tokens) {
                    try {
                        if (token.matches("\\\\red\\d+")) {
                            red = Integer.parseInt(token.substring(4));
                        } else if (token.matches("\\\\green\\d+")) {
                            green = Integer.parseInt(token.substring(6));
                        } else if (token.matches("\\\\blue\\d+")) {
                            blue = Integer.parseInt(token.substring(5));
                        }
                    } catch (NumberFormatException ex) {
                        // Parsing error due to incorrect RTF source. Simply
                        // abort parsing the color table.
                        return;
                    }
                }

                _colors.add(new Color(red, green, blue));
            } else {
                // omitted color def indicates that the default color should be
                // used for this
                // position.
                _colors.add(_defaultColor);
            }
        }
    }

}
