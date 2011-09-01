package au.org.ala.delta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import au.org.ala.delta.ui.rtf.RtfEditorPane;

/**
 * A dialog containing a read only RtfEditorPane in a scroll pane that 
 * can be used for displaying information to the user.
 */
public class RichTextDialog extends JDialog {

	private static final long serialVersionUID = 9109806191571551508L;

	private RtfEditorPane editor;
	public RichTextDialog(Window owner, String text) {
		super(owner);
		setLayout(new BorderLayout());
		editor = new RtfEditorPane();
		
		editor.setEditable(false);
		editor.setBackground(Color.WHITE);
		editor.setBorder(null);
		editor.setOpaque(true);
		editor.setFont(UIManager.getFont("Label.font"));	
		editor.setText(text);	
	
		JScrollPane scroller = new JScrollPane(editor);
		scroller.setViewportBorder(null);
		scroller.setBorder(null);
		add(scroller, BorderLayout.CENTER);
	}
	
	public void setText(String text) {
		editor.setText(text);
	}
}
