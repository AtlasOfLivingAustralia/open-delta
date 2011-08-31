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

	// private static final String HEADER_TEXT = "{\\rtf\\ansi\\deff0{\\fonttbl{\\f0\\froman Tms Rmn;}}\\pard\\plain ";
	private static final String HEADER_TEXT = "{\\rtf\\ansi\\pard\\plain ";
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
		_attributeHandlers.put(StyleConstants.Bold, new BooleanAttributeHandler(StyleConstants.Bold, CharacterAttributeType.Bold.keyword()));
		_attributeHandlers.put(StyleConstants.Italic, new BooleanAttributeHandler(StyleConstants.Italic, CharacterAttributeType.Italics.keyword()));
		_attributeHandlers.put(StyleConstants.Underline, new BooleanAttributeHandler(StyleConstants.Underline, CharacterAttributeType.Underline.keyword()));
		_attributeHandlers.put(StyleConstants.Subscript, new BooleanAttributeHandler(StyleConstants.Subscript, CharacterAttributeType.Subscript.keyword()));
		_attributeHandlers.put(StyleConstants.Superscript, new BooleanAttributeHandler(StyleConstants.Superscript, CharacterAttributeType.Superscript.keyword()));
		// _attributeHandlers.put(StyleConstants.FontSize, new FontSizeAttributeHandler(StyleConstants.FontSize, CharacterAttributeType.FontSize.keyword(), 11));
		// _attributeHandlers.put(StyleConstants.FontFamily, new FontFamilyAttributeHandler(StyleConstants.FontFamily, CharacterAttributeType.Font.keyword()));
	}

	/**
	 * Base class for all manner of attribute handlers...
	 */
	public abstract class AttributeHandler {
		protected String _rtfKeyword;
		protected Object _documentAttribute;

		protected AttributeHandler(Object documentAttribute, String rtfKeyword) {
			_documentAttribute = documentAttribute;
			_rtfKeyword = "\\" + rtfKeyword;
		}

		public abstract boolean handleAttribute(AttributeSet attributes) throws IOException;

	}

	public class FontFamilyAttributeHandler extends AttributeHandler {

		private Object _currentValue;

		protected FontFamilyAttributeHandler(Object documentAttribute, String rtfKeyword) {
			super(documentAttribute, rtfKeyword);
		}

		@Override
		public boolean handleAttribute(AttributeSet attributes) throws IOException {
			boolean handled = false;
			Object attributeValue = attributes.getAttribute(_documentAttribute);
			if (attributeValue != null && _currentValue != attributeValue) {
				// This will always force to the first font in the font table. This should actually extract a font number from a font table, but
				// this would mean buffering and deferring the entire output so that the font table could be built...
				_writer.write("\\f0");
				_currentValue = attributeValue;
				handled = true;
			}
			return handled;
		}

	}

	public class IntegerAttributeHandler extends AttributeHandler {

		private Integer _currentValue;

		public IntegerAttributeHandler(Object documentAttribute, String rtfKeyword, Integer defaultValue) {
			super(documentAttribute, rtfKeyword);
			_currentValue = defaultValue;
		}

		@Override
		public boolean handleAttribute(AttributeSet attributes) throws IOException {
			boolean handled = false;
			Integer attributeValue = (Integer) attributes.getAttribute(_documentAttribute);

			if (attributeValue != null && _currentValue != attributeValue) {
				_writer.write(_rtfKeyword);
				_writer.write("" + transform(attributeValue));
				_currentValue = attributeValue;
				handled = true;
			}
			return handled;
		}

		protected int transform(int value) {
			return value;
		}

	}

	/**
	 * Handles the font size attribute
	 */
	public class FontSizeAttributeHandler extends IntegerAttributeHandler {

		public FontSizeAttributeHandler(Object documentAttribute, String rtfKeyword, Integer defaultValue) {
			super(documentAttribute, rtfKeyword, defaultValue);
		}

		@Override
		protected int transform(int value) {
			// RTF font size is in half points
			return value * 2;
		}

	}

	/**
	 * Knows how to convert an RTF character attribute into a StyledDocument attribute.
	 */
	public class BooleanAttributeHandler extends AttributeHandler {

		private Boolean _currentState;

		public BooleanAttributeHandler(Object documentAttribute, String rtfKeyword) {
			super(documentAttribute, rtfKeyword);
			_currentState = false;
		}

		@Override
		public boolean handleAttribute(AttributeSet attributes) throws IOException {
			boolean handled = false;
			Boolean attributeValue = (Boolean) attributes.getAttribute(_documentAttribute);
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

	/**
	 * Writes just the text selected by startPos and length
	 * 
	 * @param startPos
	 * @param length
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public void writeFragment(int startPos, int length) throws IOException, BadLocationException {
		// TODO derive the font for the header from the document.
		if (_writeRtfHeader) {
			_writer.write(HEADER_TEXT);
		}

		for (int i = startPos; i < startPos + length; ++i) {
			Element chElem = _document.getCharacterElement(i);
			writeElement(chElem, i, 1);
		}

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

	private void writeElement(Element element, int pos, int length) throws BadLocationException, IOException {
		AttributeSet elementAttributes = element.getAttributes();

		writeAttributeChangesAsRTF(elementAttributes);

		if (element.isLeaf()) {
			String plainText = _document.getText(pos, length);
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
		} else {
			for (int i = 0; i < element.getElementCount(); i++) {
				writeElement(element.getElement(i));
			}
		}

	}

	private void writeElement(Element element) throws BadLocationException, IOException {
		writeElement(element, element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
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
