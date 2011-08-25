package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.document.TextDocument;

public class RedoKeyAction extends DocumentAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a RedoKeyAction instance with specific attributes.
     *
     * @param textDocument
     *            The text document.
     */
    public RedoKeyAction(TextDocument textDocument) {
        super(textDocument);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        textDocument.redo();
    }

}
