package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import javax.swing.undo.CannotUndoException;

import au.org.ala.delta.ui.codeeditor.document.TextDocument;

public class UndoKeyAction extends DocumentAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a UndoKeyAction instance with specific attributes.
     *
     * @param textDocument
     *            The text document.
     */
    public UndoKeyAction(TextDocument textDocument) {
        super(textDocument);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            textDocument.undo();
        } catch (CannotUndoException undoex) {
        }

    }

}
