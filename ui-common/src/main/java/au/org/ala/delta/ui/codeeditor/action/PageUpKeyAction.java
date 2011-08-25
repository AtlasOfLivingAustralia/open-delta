package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;

public class PageUpKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a PageUpKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public PageUpKeyAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int oldCaretPosition = codeEditor.getCaretPosition();
        int caretLine = codeEditor.getCaretLine();
        int caretColumn = oldCaretPosition - codeEditor.getLineStartOffset(caretLine);
        int visibleLines = codeEditor.getVisibleLines();
        caretLine = Math.max(caretLine - visibleLines, 0);
        int newCaretPos = Math.min(codeEditor.getLineStartOffset(caretLine) + caretColumn, codeEditor.getLineEndOffset(caretLine));

        if (isShiftPressed(e)) {
            codeEditor.select(codeEditor.getMarkPosition(), newCaretPos);
        } else {
            codeEditor.setCaretPosition(newCaretPos);
        }
    }

}
