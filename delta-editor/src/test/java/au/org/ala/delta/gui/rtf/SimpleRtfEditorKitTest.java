package au.org.ala.delta.gui.rtf;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

import junit.framework.TestCase;

import org.junit.Test;


/**
 * Tests the SimpleRtfEditorKit
 */
public class SimpleRtfEditorKitTest extends TestCase {
	
	@Test public void testReadSimpleDocument() throws Exception {
		SimpleRtfEditorKit editorKit = new SimpleRtfEditorKit();
		
		DefaultStyledDocument doc = new DefaultStyledDocument();
		InputStream in = getClass().getResourceAsStream("/rtf/test1.rtf");
		
		editorKit.read(in, doc, 0);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		editorKit.write(bout, doc, 0, doc.getLength());
		
		bout.flush();
		String documentAsString = new String(bout.toByteArray());
		
		assertEquals("This is plain text.\n", documentAsString);
		
	}
	
	@Test public void testReadSimpleFormattedDocument() throws Exception {
		SimpleRtfEditorKit editorKit = new SimpleRtfEditorKit();
		
		DefaultStyledDocument doc = new DefaultStyledDocument();
		InputStream in = getClass().getResourceAsStream("/rtf/test2.rtf");
		
		editorKit.read(in, doc, 0);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		editorKit.write(bout, doc, 0, doc.getLength());
		
		bout.flush();
		String documentAsString = new String(bout.toByteArray());
		
		System.out.println(documentAsString);
		
		
	}
	
	private void showInFrame(Document doc) throws Exception {
		JFrame f = new JFrame();
		JEditorPane p = new JEditorPane();
		p.setEditorKit(new SimpleRtfEditorKit());
		p.setDocument(doc);
		f.getContentPane().add(p, BorderLayout.CENTER);
		f.setSize(200,200);
		f.setVisible(true);
		Thread.sleep(10000);
	}
	
	
}
