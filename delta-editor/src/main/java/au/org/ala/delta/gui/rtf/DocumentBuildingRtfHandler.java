package au.org.ala.delta.gui.rtf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import au.org.ala.delta.rtf.AttributeValue;
import au.org.ala.delta.rtf.RTFHandlerAdapter;

/**
 * This class handles events from the RTFReader to build a StyledDocument suitable for
 * display by the StyledEditorKit. 
 */
public class DocumentBuildingRtfHandler extends RTFHandlerAdapter {

	/** Buffers text until it is ready to be inserted into the Document */
	private StringBuilder _textBuffer;
	/** The set of attributes to be applied to the text in the _textBuffer */
	private MutableAttributeSet _currentAttributes;
	
	/** The document we are building */
	private DefaultStyledDocument _document;
	
	/** Handles for RTF attributes */
	private Map<String, AttributeHandler> _attributeHandlers = new HashMap<String, AttributeHandler>();
	
	public void configureAttributeHandlers() {
		_attributeHandlers.put("b", new AttributeHandler(StyleConstants.Bold));
		_attributeHandlers.put("i", new AttributeHandler(StyleConstants.Italic));
		_attributeHandlers.put("u", new AttributeHandler(StyleConstants.Underline));
		_attributeHandlers.put("sub", new AttributeHandler(StyleConstants.Subscript));
		_attributeHandlers.put("super", new AttributeHandler(StyleConstants.Superscript));
	}
	
	/**
	 * Knows how to convert an RTF character attribute into a StyledDocument attribute.
	 */
	public static class AttributeHandler {
		private Object _styleAttribute;
		public AttributeHandler(Object styleAttribute) {
			_styleAttribute = styleAttribute;
		}
		
		public void handleAttribute(AttributeValue attr, MutableAttributeSet newAttributes) {
			newAttributes.addAttribute(_styleAttribute, Boolean.valueOf(!attr.hasParam()));
			
		}
	}
	
	
	
	public DocumentBuildingRtfHandler(DefaultStyledDocument document) {
		configureAttributeHandlers();
		_currentAttributes = new SimpleAttributeSet();
		_document = document;
		_textBuffer = new StringBuilder();
	}

	private char _previousChar;
	@Override
	public void onTextCharacter(char ch) {

		if (ch == 0) {
			return;
		}
		if (ch > 0xFF) {
			insertUnicodeCodePoint(ch);
		}
		else {
			// Convert \r to \n as the editor pane ignores \r.  Not sure what is 
			// happening to the \n's... they don't seem to be coming through.
			if (ch != '\r' ) {
				if (_previousChar == '\r' && ch != '\n') {
					_textBuffer.append('\n');
				}
				_textBuffer.append(ch);
			}
		}
		_previousChar = ch;
	}
	
	private void insertUnicodeCodePoint(char ch) {
		_textBuffer.append("\\u").append(Integer.toString(ch));
		// we are now supposed to write our "best ascii representation of the char.  
		_textBuffer.append("?");
	}
	
	@Override
	public void endParse() {
		appendToDocument();
	}

	@Override
	public void onCharacterAttributeChange(List<AttributeValue> values) {
		
		MutableAttributeSet newAttributes = new SimpleAttributeSet();
		newAttributes.addAttributes(_currentAttributes);
		
		handleAttributeChanges(values, newAttributes);
		if (!newAttributes.equals(_currentAttributes)){
			appendToDocument();
			_currentAttributes = newAttributes;
		}
	}
	
	/**
	 * Attempts to find a handler for each AttributeValue in the list.
	 * @param values the AttributeValues that have changed.
	 * @param newAttributes a container for any changes to the StyledDocument attributes that 
	 * should be applied.
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
	 * Appends the current text buffer to the end of the Document we are building with the 
	 * set of current attributes.
	 */
	private void appendToDocument() {
		try {
			_document.insertString(_document.getLength(), _textBuffer.toString(), _currentAttributes);
			_textBuffer = new StringBuilder();
		}
		catch (BadLocationException e) {
			throw new RuntimeException("Parsing the RTF document failed!", e);
		}
	}

}
