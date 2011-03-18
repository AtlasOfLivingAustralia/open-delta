package au.org.ala.delta.ui.rtf;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import au.org.ala.delta.rtf.CharacterAttributeType;
import au.org.ala.delta.rtf.CharacterKeyword;
import au.org.ala.delta.rtf.Keyword;

/**
 * Writes the contents of a StyledDocument as RTF encoded text.
 */
public class RTFWriter {

	private static final String HEADER_TEXT = "{\\rtf\\ansi\\deff0{\\fonttbl{\\f0\\froman Tms Rmn;}}\\pard\\plain ";
	private static final String TRAILING_TEXT = "}";
	
	/** The output stream we are writing to */
	private Writer _writer;
	
	/** The Document we are building */
	private StyledDocument _document;
	
	/** Whether or not to write the RTF header - default is true */
	private boolean _writeRtfHeader;
	
	
	
	/** Handles for RTF attributes */
	private Map<Object, AttributeHandler> _attributeHandlers = new HashMap<Object, AttributeHandler>();
	
	public RTFWriter(Writer Writer, StyledDocument document) {
		this(Writer, document, true);
	}
	
	public RTFWriter(Writer Writer, StyledDocument document, boolean writeRtfHeader) {
		_writer = Writer;
		_document = document;
		_writeRtfHeader = writeRtfHeader;
		
		configureAttributeHandlers();
	}
	
	
	private void configureAttributeHandlers() {
		_attributeHandlers.put(StyleConstants.Bold, new AttributeHandler(StyleConstants.Bold, CharacterAttributeType.Bold.keyword()));
		_attributeHandlers.put(StyleConstants.Italic, new AttributeHandler(StyleConstants.Italic, CharacterAttributeType.Italics.keyword()));
		_attributeHandlers.put(StyleConstants.Underline, new AttributeHandler(StyleConstants.Underline, CharacterAttributeType.Underline.keyword()));
		_attributeHandlers.put(StyleConstants.Subscript, new AttributeHandler(StyleConstants.Subscript, CharacterAttributeType.Subscript.keyword()));
		_attributeHandlers.put(StyleConstants.Superscript, new AttributeHandler(StyleConstants.Superscript, CharacterAttributeType.Superscript.keyword()));
	}
	
	/**
	 * Knows how to convert an RTF character attribute into a StyledDocument attribute.
	 */
	public class AttributeHandler {
		private String _rtfKeyword;
		private Object _documentAttribute;
		private Boolean _currentState;
		public AttributeHandler(Object documentAttribute, String rtfKeyword) {
			_documentAttribute = documentAttribute;
			_rtfKeyword = "\\"+rtfKeyword;
			_currentState = false;
		}
		
		public boolean handleAttribute(AttributeSet attributes) throws IOException {
			boolean handled = false;
			Boolean attributeValue = (Boolean)attributes.getAttribute(_documentAttribute);
			if (attributeValue == null) {
				attributeValue = Boolean.FALSE;
			}
			if (_currentState != attributeValue) {
				_writer.write(_rtfKeyword);
				if (Boolean.FALSE.equals(attributeValue)) {
					_writer.write('0');
				}
				_currentState = attributeValue.booleanValue();
				handled = true;
			}
			return handled;
		}
	}
	
	/**
	 * Writes the contents of the StyledDocument to the Writer.
	 */
	public void write() throws IOException, BadLocationException {
		
		// TODO derive the font for the header from the document.
		if (_writeRtfHeader) {
			_writer.write(HEADER_TEXT);
		}
		Element docRoot = _document.getDefaultRootElement();
		
		writeElement(docRoot);
		
		closeOpenAttributes();
		
		if (_writeRtfHeader) {
			_writer.write(TRAILING_TEXT);
		}
		
		_writer.flush();
	}
	
	private void closeOpenAttributes() throws IOException {
		SimpleAttributeSet emptyAttributes = new SimpleAttributeSet();
		for (AttributeHandler handler : _attributeHandlers.values()) {
			handler.handleAttribute(emptyAttributes);
		}
	}
	

	private void writeElement(Element element) throws BadLocationException, IOException {
		
		AttributeSet elementAttributes = element.getAttributes();
		
		writeAttributeChangesAsRTF(elementAttributes);
		
		if (element.isLeaf()) {
			String plainText = _document.getText(element.getStartOffset(), element.getEndOffset()-element.getStartOffset());
			for (int i = 0; i < plainText.length(); ++i) {
				char ch = plainText.charAt(i);
				if (ch > 127) {
					CharacterKeyword kwd = Keyword.findKeywordForCharacter(ch);
					if (kwd != null) {
						_writer.write("\\");
						_writer.write(kwd.getKeyword());
						_writer.write(" ");
					} else {
						_writer.write("\\u");
						_writer.write(Integer.toString(ch));
						_writer.write("?");
					}
				} else {
					if (ch == '\n') {
						_writer.write("\\par ");
					} else {
						_writer.write(ch);
					}
				}
			}
		}
		else  {
			for (int i=0; i<element.getElementCount(); i++) {
				writeElement(element.getElement(i));
			}
		}
	}
	

	private void writeAttributeChangesAsRTF(AttributeSet elementAttributes) throws IOException {
	
		boolean changed = false;

		for (AttributeHandler handler : _attributeHandlers.values()) {
			boolean handled = handler.handleAttribute(elementAttributes);
			changed = changed || handled;
		}
		
		if (changed) {
			// write a trailing space after the control characters.
			_writer.write(' ');
		}
		
	}
}
