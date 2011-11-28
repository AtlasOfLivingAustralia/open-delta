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

import au.org.ala.delta.ui.codeeditor.CodeTextArea;
import au.org.ala.delta.ui.codeeditor.TextUtilities;

public class LeftKeyAction extends KeyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a LeftKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public LeftKeyAction(CodeTextArea textArea) {
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

            if (isShiftPressed(e)) {
                codeEditor.select(codeEditor.getMarkPosition(), lineStart + caret);
            } else {
                codeEditor.setCaretPosition(lineStart + caret);
            }
        } else {
            if (caret == 0) {
                codeEditor.getToolkit().beep();
                return;
            }

            if (isShiftPressed(e)) {
                codeEditor.select(codeEditor.getMarkPosition(), caret - 1);
            } else {
                codeEditor.setCaretPosition(caret - 1);
            }
        }
    }

}
