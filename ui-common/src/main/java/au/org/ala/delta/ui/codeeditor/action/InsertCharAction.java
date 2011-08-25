package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;

public class InsertCharAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a InsertCharAction instance with specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public InsertCharAction(CodeEditor textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();
        if (codeEditor.isEditable()) {
            StringBuffer buf = new StringBuffer();
            buf.append(str);
            codeEditor.overwriteSetSelectedText(buf.toString());
        } else {
            codeEditor.getToolkit().beep();
        }
    }

}
