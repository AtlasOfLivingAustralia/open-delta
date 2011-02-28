package au.org.ala.delta.gui.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

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

	@Override
	public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
		RTFHandler handler = new DocumentBuildingRtfHandler((DefaultStyledDocument)doc);
		RTFReader reader = new RTFReader(in, handler);
		reader.parse();
	}
	
	@Override
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
		throw new UnsupportedOperationException("Please use read(InputStream, Document, int) instead");
	}
	
	@Override
	public void write(OutputStream out, Document doc, int pos, int len) throws IOException, BadLocationException {
		
		if (doc instanceof StyledDocument) {
			new RTFWriter(out, (StyledDocument)doc).write();
		}
	}
	
	
	

}
