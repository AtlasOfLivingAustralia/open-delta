package au.org.ala.delta.ui.rtf;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

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

    public void configureAttributeHandlers() {
        _attributeHandlers.put(CharacterAttributeType.Bold.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Bold));
        _attributeHandlers.put(CharacterAttributeType.Italics.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Italic));
        _attributeHandlers.put(CharacterAttributeType.Underline.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Underline));
        _attributeHandlers.put(CharacterAttributeType.Subscript.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Subscript));
        _attributeHandlers.put(CharacterAttributeType.Superscript.keyword(), new SimpleBooleanAttributeHandler(StyleConstants.Superscript));
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

    public static class FontAttributeHandler implements AttributeHandler {
        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            newAttributes.addAttribute(StyleConstants.FontFamily, "Roman");
        }
    }

    public static class FontSizeAttributeHandler implements AttributeHandler {

        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            int fontSize = attr.getParam() / 2;
            newAttributes.addAttribute(StyleConstants.FontSize, fontSize);
        }

    }

    public static class FontColorAttributeHandler implements AttributeHandler {
        @Override
        public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
            newAttributes.addAttribute(StyleConstants.Foreground, Color.RED);
        }
    }

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

    @Override
    public void onKeyword(String keyword, boolean hasParam, int param) {

    }

    public DocumentBuildingRtfHandler(DefaultStyledDocument document) {
        configureAttributeHandlers();
        _currentCharacterAttributes = new SimpleAttributeSet();
        _currentParagraphAttributes = new SimpleAttributeSet();
        _document = document;
        _textBuffer = new StringBuilder();
    }

    private char _previousChar;

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
            _document.insertString(_document.getLength(), text, _currentCharacterAttributes);
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

}
