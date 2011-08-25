package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;

public class UpKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a UpKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public UpKeyAction(CodeEditor textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int caret = codeEditor.getCaretPosition();
        int line = codeEditor.getCaretLine();

        if (line == 0) {
            codeEditor.getToolkit().beep();
            return;
        }

        int magic = codeEditor.getMagicCaretPosition();
        if (magic == -1) {
            magic = codeEditor.offsetToX(line, caret - codeEditor.getLineStartOffset(line));
        }

        caret = codeEditor.getLineStartOffset(line - 1) + codeEditor.xToOffset(line - 1, magic);
        if (isShiftPressed(e)) {
            codeEditor.select(codeEditor.getMarkPosition(), caret);
        } else {
            codeEditor.setCaretPosition(caret);
        }
        codeEditor.setMagicCaretPosition(magic);
    }

}
