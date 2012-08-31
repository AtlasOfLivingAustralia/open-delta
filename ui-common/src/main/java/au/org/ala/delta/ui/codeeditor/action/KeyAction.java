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
import java.awt.event.InputEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;

public abstract class KeyAction extends EditorAction {
	
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a KeyAction instance with no specific arguments.
     *
     * @param textArea
     */
    public KeyAction(CodeTextArea textArea) {
        super(textArea);
    }

    /**
     * Checks whether the shift key is pressed or not.
     *
     * @param evt
     *            The action event.
     * @return true, if shift key is pressed, else false.
     */
    boolean isShiftPressed(ActionEvent evt) {
        return (evt.getModifiers() & InputEvent.SHIFT_MASK) > 0;
    }

    /**
     * Checks whether the alt key is pressed or not.
     *
     * @param evt
     *            The action event.
     * @return true, if alt key is pressed, else false.
     */
    boolean isAltPressed(ActionEvent evt) {
        return (evt.getModifiers() & InputEvent.ALT_MASK) > 0;
    }

    /**
     * Checks whether the ctrl key is pressed or not.
     *
     * @param evt
     *            The action event.
     * @return true, if ctrl key is pressed, else false.
     */
    boolean isCtrlPressed(ActionEvent evt) {
        return (evt.getModifiers() & InputEvent.CTRL_MASK) > 0;
    }
}
