package au.org.ala.delta.ui.codeeditor;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;

public class TestHarness extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException {

		TestHarness frm = new TestHarness();

		frm.setVisible(true);
		frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public TestHarness() throws IOException {
		super();		
		this.setSize(new Dimension(400, 400));
		CodeEditor editor = new CodeEditor("text/confor");
		
		String d = FileUtils.readFileToString(new File("c:/zz/specs"));
		
		editor.setText(d);

		editor.setEOLMarkersPainted(false);
		editor.setShowLineNumbers(true);

		this.getContentPane().add(editor);
	}
}
