package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;

public class FindNextAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a FindNextAction instance with specific arguments.
     *
     * @param textArea
     *            The text editor.
     */
    public FindNextAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        codeEditor.findNext();
    }

}
