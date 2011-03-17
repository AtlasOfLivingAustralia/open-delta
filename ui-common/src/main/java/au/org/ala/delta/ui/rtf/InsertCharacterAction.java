package au.org.ala.delta.ui.rtf;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class InsertCharacterAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private RtfEditor _editor;
	private char _char;

	public InsertCharacterAction(RtfEditor editor, char ch) {
		_editor = editor;
		_char = ch;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_editor.insertCharAtCaret(_char);
	}
}
