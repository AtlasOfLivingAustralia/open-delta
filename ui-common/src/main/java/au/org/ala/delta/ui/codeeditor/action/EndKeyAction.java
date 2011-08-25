package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;

public class EndKeyAction extends KeyAction {

  /**
     *
     */
    private static final long serialVersionUID = 1L;

/**
   * Constructs a EndKeyAction instance with specific arguments.
   * @param textArea The text area.
   */
  public EndKeyAction(CodeTextArea textArea) {
    super(textArea);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {

    if (isCtrlPressed(e)) {
      if (isShiftPressed(e)) {
        codeEditor.select(codeEditor.getMarkPosition(), codeEditor.getDocumentLength());
      } else {
        codeEditor.setCaretPosition(codeEditor.getDocumentLength());
      }
    } else {
      int caret = codeEditor.getCaretPosition();

      int lastOfLine = codeEditor.getLineEndOffset(codeEditor.getCaretLine()) - 1;

      int lastDocument = codeEditor.getDocumentLength();

      if (caret == lastDocument) {
        codeEditor.getToolkit().beep();
        return;
      } else {
        caret = lastOfLine;
      }

      if (isShiftPressed(e)) {
        codeEditor.select(codeEditor.getMarkPosition(), caret);
      } else {
        codeEditor.setCaretPosition(caret);
      }
    }
  }

}
