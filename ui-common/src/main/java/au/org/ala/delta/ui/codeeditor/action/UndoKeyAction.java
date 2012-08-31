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

import javax.swing.undo.CannotUndoException;

import au.org.ala.delta.ui.codeeditor.document.TextDocument;

public class UndoKeyAction extends DocumentAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a UndoKeyAction instance with specific attributes.
     *
     * @param textDocument
     *            The text document.
     */
    public UndoKeyAction(TextDocument textDocument) {
        super(textDocument);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            textDocument.undo();
        } catch (CannotUndoException undoex) {
        }

    }

}
