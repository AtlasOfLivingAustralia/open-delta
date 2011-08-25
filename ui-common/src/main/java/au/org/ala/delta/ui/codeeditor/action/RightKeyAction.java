package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;
import au.org.ala.delta.ui.codeeditor.TextUtilities;

public class RightKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a RightKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public RightKeyAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int caret = codeEditor.getCaretPosition();

        if (isCtrlPressed(e)) {
            int line = codeEditor.getCaretLine();
            int lineStart = codeEditor.getLineStartOffset(line);
            caret -= lineStart;

            String lineText = codeEditor.getLineText(codeEditor.getCaretLine());

            if (caret == lineText.length()) {
                if (lineStart + caret == codeEditor.getDocumentLength()) {
                    codeEditor.getToolkit().beep();
                    return;
                }
                caret++;
            } else {
                String noWordSep = (String) codeEditor.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordEnd(lineText, caret, noWordSep);
            }

            if (isShiftPressed(e)) {
                codeEditor.select(codeEditor.getMarkPosition(), lineStart + caret);
            } else {
                codeEditor.setCaretPosition(lineStart + caret);
            }
        } else {
            if (caret == codeEditor.getDocumentLength()) {
                codeEditor.getToolkit().beep();
                return;
            }

            if (isShiftPressed(e)) {
                codeEditor.select(codeEditor.getMarkPosition(), caret + 1);
            } else {
                codeEditor.setCaretPosition(caret + 1);
            }
        }
    }

}
