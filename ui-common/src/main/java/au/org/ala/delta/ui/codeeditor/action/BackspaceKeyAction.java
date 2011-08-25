package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;
import au.org.ala.delta.ui.codeeditor.TextUtilities;

public class BackspaceKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a BackspaceKeyAction instance with specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public BackspaceKeyAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        if (isCtrlPressed(e)) {
            int start = codeEditor.getSelectionStart();
            if (start != codeEditor.getSelectionEnd()) {
                codeEditor.insertText("");
            }

            int line = codeEditor.getCaretLine();
            int lineStart = codeEditor.getLineStartOffset(line);
            int caret = start - lineStart;

            String lineText = codeEditor.getLineText(codeEditor.getCaretLine());

            if (caret == 0) {
                if (lineStart == 0) {
                    codeEditor.getToolkit().beep();
                    return;
                }
                caret--;
            } else {
                String noWordSep = (String) codeEditor.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordStart(lineText, caret, noWordSep);
            }

            try {
                codeEditor.getDocument().remove(caret + lineStart, start - (caret + lineStart), true);
            } catch (BadLocationException bl) {
                bl.printStackTrace();
            }
        } else {
            if (!codeEditor.isEditable()) {
                codeEditor.getToolkit().beep();
                return;
            }

            if (codeEditor.getSelectionStart() != codeEditor.getSelectionEnd()) {
                codeEditor.insertText("");
            } else {
                int caret = codeEditor.getCaretPosition();
                if (caret == 0) {
                    codeEditor.getToolkit().beep();
                    return;
                }
                try {
                    codeEditor.getDocument().remove(caret - 1, 1, true);
                } catch (BadLocationException bl) {
                    bl.printStackTrace();
                }
            }
        }
    }
}
