package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;

public class TabKeyAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TabKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public TabKeyAction(CodeEditor textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (!codeEditor.isEditable()) {
            codeEditor.getToolkit().beep();
            return;
        }

        if (codeEditor.getSelectionStartLine() != codeEditor.getSelectionEndLine()) {
            if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
                codeEditor.unindentSelectedLines();
            } else {
                codeEditor.indentSelectedLines();
            }
        } else {
            if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
                codeEditor.unindentSelectedLines();
            } else {
                int selstart = codeEditor.getSelectionStart();
                int selend = codeEditor.getSelectionEnd();
                int linestart = codeEditor.getLineStartOffset(codeEditor.getSelectionStartLine());
                int lineend = codeEditor.getLineEndOffset(codeEditor.getSelectionStartLine());
                if ((selstart == linestart) && (selend == lineend)) {
                    codeEditor.indentSelectedLines();
                } else {
                    codeEditor.overwriteSetSelectedText("");
                    codeEditor.insertTab(codeEditor.getCaretPosition());
                }
            }
        }
    }

}
