package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;

public class GotoLineKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a GotoLineKeyAction instance with specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public GotoLineKeyAction(CodeEditor textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        codeEditor.showGotoLineDialog();
    }

}
