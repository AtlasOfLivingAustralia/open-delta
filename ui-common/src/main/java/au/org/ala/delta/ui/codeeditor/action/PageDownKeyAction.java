package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;

public class PageDownKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a PageDownKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public PageDownKeyAction(CodeEditor textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int lineCount = codeEditor.getLineCount();
        int oldCaretPosition = codeEditor.getCaretPosition();
        int caretLine = codeEditor.getCaretLine();
        int caretColumn = oldCaretPosition - codeEditor.getLineStartOffset(caretLine);
        int visibleLines = codeEditor.getVisibleLines();
        caretLine = Math.min(caretLine + visibleLines, lineCount - 1);
        int newCaretPos = Math.min(codeEditor.getLineStartOffset(caretLine) + caretColumn, codeEditor.getLineEndOffset(caretLine));

        if (isShiftPressed(e)) {
            codeEditor.select(codeEditor.getMarkPosition(), newCaretPos);
        } else {
            codeEditor.setCaretPosition(newCaretPos);
        }
    }

}
