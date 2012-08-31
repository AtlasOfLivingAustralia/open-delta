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
    public EnterKeyAction(CodeTextArea textArea) {
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
