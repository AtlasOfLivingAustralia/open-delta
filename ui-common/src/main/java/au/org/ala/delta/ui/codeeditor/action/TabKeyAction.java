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

public class TabKeyAction extends EditorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TabKeyAction instance with no specific arguments.
     *
     * @param textArea
     *            The text area.
     */
    public TabKeyAction(CodeTextArea textArea) {
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

        if (codeEditor.getSelectionStartLine() != codeEditor.getSelectionEndLine()) {
            if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
                codeEditor.unindentSelectedLines();
            } else {
                codeEditor.indentSelectedLines();
            }
        } else {
            if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
                codeEditor.unindentSelectedLines();
            } else {
                int selstart = codeEditor.getSelectionStart();
                int selend = codeEditor.getSelectionEnd();
                int linestart = codeEditor.getLineStartOffset(codeEditor.getSelectionStartLine());
                int lineend = codeEditor.getLineEndOffset(codeEditor.getSelectionStartLine());
                if ((selstart == linestart) && (selend == lineend)) {
                    codeEditor.indentSelectedLines();
                } else {
                    codeEditor.overwriteSetSelectedText("");
                    codeEditor.insertTab(codeEditor.getCaretPosition());
                }
            }
        }
    }

}
