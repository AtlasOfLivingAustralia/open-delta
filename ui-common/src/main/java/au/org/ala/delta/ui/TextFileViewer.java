package au.org.ala.delta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

/**
 * A dialog containing a read only EditorPane in a scroll pane that 
 * can be used for displaying plain text information to the user.
 */
public class TextFileViewer extends JDialog {

	private static final long serialVersionUID = 9109806191571551508L;

	private JTextPane viewer;
	public TextFileViewer(Window owner, File file) throws IOException {
		super(owner);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		viewer = new JTextPane();
		
		viewer.setEditable(false);
		viewer.setBackground(Color.WHITE);
		viewer.setBorder(null);
		viewer.setOpaque(true);
		viewer.setFont(UIManager.getFont("Label.font"));	
		
		JScrollPane scroller = new JScrollPane(viewer);
		getContentPane().add(scroller, BorderLayout.CENTER);
		
		setTitle(file.getAbsolutePath());
		displayFileContents(file);
		pack();
	}
	
	private void displayFileContents(File file) throws IOException {
		String text = FileUtils.readFileToString(file);
		viewer.setText(text);
	}
}
