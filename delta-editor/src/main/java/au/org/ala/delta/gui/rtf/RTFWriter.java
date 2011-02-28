package au.org.ala.delta.gui.rtf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Writes the contents of a StyledDocument as RTF encoded text.
 */
public class RTFWriter {

	/** The output stream we are writing to */
	private OutputStream _outputStream;
	
	private StyledDocument _document;
	
	/** Handles for RTF attributes */
	private Map<Object, AttributeHandler> _attributeHandlers = new HashMap<Object, AttributeHandler>();
	
	public RTFWriter(OutputStream outputStream, StyledDocument document) {
		_outputStream = outputStream;
		_document = document;
		
		configureAttributeHandlers();
	}
	
	
	private void configureAttributeHandlers() {
		_attributeHandlers.put(StyleConstants.Bold, new AttributeHandler(StyleConstants.Bold, "b"));
		_attributeHandlers.put(StyleConstants.Italic, new AttributeHandler(StyleConstants.Italic, "i"));
		_attributeHandlers.put(StyleConstants.Underline, new AttributeHandler(StyleConstants.Underline, "u"));
		_attributeHandlers.put(StyleConstants.Subscript, new AttributeHandler(StyleConstants.Subscript, "sub"));
		_attributeHandlers.put(StyleConstants.Superscript, new AttributeHandler(StyleConstants.Superscript, "super"));
	}
	
	/**
	 * Knows how to convert an RTF character attribute into a StyledDocument attribute.
	 */
	public class AttributeHandler {
		private byte[] _rtfKeyword;
		private Object _documentAttribute;
		private Boolean _currentState;
		public AttributeHandler(Object documentAttribute, String rtfKeyword) {
			_documentAttribute = documentAttribute;
			_rtfKeyword = ("\\"+rtfKeyword).getBytes();
			_currentState = false;
		}
		
		public boolean handleAttribute(AttributeSet attributes) throws IOException {
			boolean handled = false;
			Boolean attributeValue = (Boolean)attributes.getAttribute(_documentAttribute);
			if (attributeValue == null) {
				attributeValue = Boolean.FALSE;
			}
			if (_currentState != attributeValue) {
				_outputStream.write(_rtfKeyword);
				if (Boolean.FALSE.equals(attributeValue)) {
					_outputStream.write('0');
				}
				_currentState = attributeValue.booleanValue();
				handled = true;
			}
			return handled;
		}
	}
	
	/**
	 * Writes the contents of the StyledDocument to the OutputStream.
	 */
	public void write() throws IOException, BadLocationException {
		MutableAttributeSet activeAttributes = new SimpleAttributeSet();
		
		Element docRoot = _document.getDefaultRootElement();
		
		writeElement(docRoot, activeAttributes);
		
		closeOpenAttributes();
	}
	
	private void closeOpenAttributes() throws IOException {
		SimpleAttributeSet emptyAttributes = new SimpleAttributeSet();
		for (AttributeHandler handler : _attributeHandlers.values()) {
			handler.handleAttribute(emptyAttributes);
		}
	}
	

	private void writeElement(Element element, MutableAttributeSet activeAttributes) throws BadLocationException, IOException {
		
		AttributeSet elementAttributes = element.getAttributes();
		
		writeAttributeChangesAsRTF(activeAttributes, elementAttributes);
		
		if (element.isLeaf()) {
			String plainText = _document.getText(element.getStartOffset(), element.getEndOffset()-element.getStartOffset());
			_outputStream.write(plainText.getBytes());
		}
		else  {
			for (int i=0; i<element.getElementCount(); i++) {
				writeElement(element.getElement(i), activeAttributes);
			}
		}
	}
	

	private void writeAttributeChangesAsRTF(MutableAttributeSet activeAttributes,
			AttributeSet elementAttributes) throws IOException {
	
		boolean changed = false;
		Enumeration e = elementAttributes.getAttributeNames();
		while (e.hasMoreElements()) {
			AttributeHandler handler = _attributeHandlers.get(e.nextElement());
			if (handler != null) {
				boolean handled = handler.handleAttribute(elementAttributes);
				changed = changed || handled;
			}
		}
		if (changed) {
			// write a trailing space after the control characters.
			_outputStream.write(' ');
		}
		
	}
}
