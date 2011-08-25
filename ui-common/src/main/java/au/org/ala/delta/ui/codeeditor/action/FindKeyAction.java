package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;

public class FindKeyAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a FindKeyAction instance with specific arguments.
     *
     * @param textArea
     *            The text editor.
     */
    public FindKeyAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * Is called on key stroke.
     *
     * @param e
     *            The event.
     */
    public void actionPerformed(ActionEvent e) {
        codeEditor.showFindDialog();
    }
}
