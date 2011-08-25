package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;

public class SelectAllAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a SelectAllAction instance with specific attributes.
     *
     * @param textArea
     *            The text area to perform actions on.
     */
    public SelectAllAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        codeEditor.selectAll();
    }

}
