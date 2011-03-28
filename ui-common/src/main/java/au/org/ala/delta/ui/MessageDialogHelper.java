package au.org.ala.delta.ui;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.apache.commons.lang.WordUtils;

/**
 * Helper class for displaying multi-line messages using a JTextArea as the message component for
 * JOptionPane.
 */
public class MessageDialogHelper {

	/**
	 * Uses JOptionPane.showInputDialog to display the supplied (multi-line) message and return the
	 * user input.
	 * @param parent the parent component used to display the JOptionPane.
	 * @param title the title for the option pane.
	 * @param text the message text to display on the option pane.  Multi-line messages should 
	 * use the "\n" character.
	 * @param numColumns the column position to wrap the text at.
	 * @param initialValue the initial value for the user input.
	 * @return the value supplied to the JOptionPane input dialog.
	 */
	public static String showInputDialog(Component parent, String title, String text, int numColumns, String initialValue) {
		
		JTextArea messageDisplay = createMessageDisplay(text, numColumns);
		return (String)JOptionPane.showInputDialog(parent, messageDisplay, title, JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
	}
	
	/**
	 * Creates a text area that looks like a JLabel that has a preferredSize calculated to fit
	 * all of the supplied text wrapped at the supplied column. 
	 * @param text the text to display in the JTextArea.
	 * @param numColumns the column number to wrap text at.
	 * @return a new JTextArea configured for the supplied text.
	 */
	private static JTextArea createMessageDisplay(String text, int numColumns) {
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);

		textArea.setBackground(UIManager.getColor("Label.background"));
		textArea.setFont(UIManager.getFont("Label.font"));
		
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setColumns(numColumns);
		
		String wrapped = WordUtils.wrap(text, numColumns);
		textArea.setRows(wrapped.split("\n").length-1);
		
		textArea.setText(text);
		
		return textArea;
		
	}
}
