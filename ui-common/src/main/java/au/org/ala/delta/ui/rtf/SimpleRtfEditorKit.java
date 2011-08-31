package au.org.ala.delta.ui.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import au.org.ala.delta.rtf.RTFHandler;
import au.org.ala.delta.rtf.RTFReader;

/**
 * An editor kit that understands the RTF formatting required by the DELTA application.
 */
public class SimpleRtfEditorKit extends StyledEditorKit {

	private static final long serialVersionUID = -992062272094990061L;
	
	private JComponent _owner;
	
	public SimpleRtfEditorKit(JComponent owner) {
		_owner = owner;
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

}
