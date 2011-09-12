package au.org.ala.delta.ui.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JComponent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import au.org.ala.delta.rtf.RTFHandler;
import au.org.ala.delta.rtf.RTFReader;

/**
 * An editor kit that understands the RTF formatting required by the DELTA application.
 */
public class SimpleRtfEditorKit extends StyledEditorKit {

	private static final long serialVersionUID = -992062272094990061L;

	private JComponent _owner;
	private boolean _centerVertically = false;

	public SimpleRtfEditorKit(JComponent owner) {
		this(owner, false);
	}
	
	public SimpleRtfEditorKit(JComponent owner, boolean centerVertically) {
		_owner = owner;
		_centerVertically = centerVertically;
	}
	

	@Override
	public ViewFactory getViewFactory() {
		if (_centerVertically) {
			return new CenteringViewFactory();
		}
		return super.getViewFactory();
	}

	/**
	 * @return "text/rtf"
	 */
	public String getContentType() {
		return "text/rtf";
	}

	@Override
	public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
		// TODO here in theory we should read the RTF header to determine the encoding then
		// create the appropriate reader.
		parseRtf(new InputStreamReader(in), doc, pos);
	}

	@Override
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
		// In this case it is up to the consumer to be using the correct character encoding.
		parseRtf(in, doc, pos);
	}

	private void parseRtf(Reader in, Document doc, int position) throws IOException {
		RTFHandler handler = new DocumentBuildingRtfHandler((DefaultStyledDocument) doc, position, _owner);
		RTFReader reader = new RTFReader(in, handler);
		reader.parse();
	}

	@Override
	public void write(OutputStream out, Document doc, int pos, int len) throws IOException, BadLocationException {
		// TODO if we are creating a writer like this we need to match the code page with the code page
		// in the document.
		write(new OutputStreamWriter(out), doc, pos, len);
	}

	@Override
	public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
		if (doc instanceof StyledDocument) {
			StyledDocument styledDoc = (StyledDocument) doc;
			RTFWriter writer = new RTFWriter(out, styledDoc);
			if (len == styledDoc.getLength() || len == 0) {
				writer.write();
			} else {
				writer.writeFragment(pos, len);
			}
		} else {
			super.write(out, doc, pos, len);
		}
	}

	public void writeBody(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
		if (doc instanceof StyledDocument) {
			new RTFWriter(out, (StyledDocument) doc, false).write();
		} else {
			super.write(out, doc, pos, len);
		}
	}

	static class CenteringViewFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {

					return new LabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new CenteredBoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {

					return new IconView(elem);
				}
			}

			return new LabelView(elem);
		}

	}
	
}

class CenteredBoxView extends BoxView {
	public CenteredBoxView(Element elem, int axis) {

		super(elem, axis);
	}

	protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
		super.layoutMajorAxis(targetSpan, axis, offsets, spans);		
		int textBlockHeight = 0;
		int offset = 0;

		for (int i = 0; i < spans.length; i++) {
			textBlockHeight = spans[i];
		}
		
		offset = (targetSpan - textBlockHeight) / 2;
		
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] += offset;
		}

	}
}