/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;
import au.org.ala.delta.ui.codeeditor.TextUtilities;

public class DeleteKeyAction extends KeyAction {

  /**
     *
     */
    private static final long serialVersionUID = 1L;

/**
   * Constructs a DeleteKeyAction instance with specific arguments.
   * @param textArea The text area.
   */
  public DeleteKeyAction(CodeTextArea textArea) {
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

    try {
      codeEditor.getDocument().remove(start, (caret + lineStart) - start, true);
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
      if (caret == codeEditor.getDocumentLength()) {
        codeEditor.getToolkit().beep();
        return;
      }
      try {
        codeEditor.getDocument().remove(caret, 1);
      } catch (BadLocationException bl) {
        bl.printStackTrace();
      }
    }
  }
  }

}
