package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;

import au.org.ala.delta.ui.codeeditor.CodeEditor;

public class EnterKeyAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a EnterKeyAction instance with specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public EnterKeyAction(CodeEditor textArea) {
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
        String currentLine = codeEditor.getLineText(codeEditor.getCaretLine());
        StringBuilder prefix = new StringBuilder();
        int prefixlen = codeEditor.getCaretPosition() - codeEditor.getLineStartOffset(codeEditor.getCaretLine());
        for (int i = prefixlen; i < currentLine.length(); ++i) {
            char ch = currentLine.charAt(i);
            if (ch == ' ' || ch == '\t') {
                try {
                    codeEditor.getDocument().remove(codeEditor.getCaretPosition(), 1);
                } catch (BadLocationException blex) {
                    throw new RuntimeException(blex);
                }
            } else {
                break;
            }
        }

        if (currentLine.startsWith("\t") || currentLine.startsWith(" ")) {
            for (char ch : currentLine.toCharArray()) {
                if (ch == '\t' || ch == ' ') {
                    prefix.append(ch);
                } else {
                    break;
                }
                if (prefix.length() >= prefixlen) {
                    break;
                }
            }
        }
        codeEditor.insertText("\n" + prefix.toString());
    }

}
