package au.org.ala.delta.ui.codeeditor.action;

import javax.swing.AbstractAction;

import au.org.ala.delta.ui.codeeditor.document.TextDocument;

public abstract class DocumentAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	/** The text document. */
    protected TextDocument textDocument;

    /**
     * Constructs a DocumentAction instance with specific attributes.
     *
     * @param textDocument
     *            The text document.
     */
    public DocumentAction(TextDocument textDocument) {
        this.textDocument = textDocument;
    }

}