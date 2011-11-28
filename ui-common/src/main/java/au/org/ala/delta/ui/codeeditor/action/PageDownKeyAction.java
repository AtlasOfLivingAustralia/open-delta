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
    public PageDownKeyAction(CodeTextArea textArea) {
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
