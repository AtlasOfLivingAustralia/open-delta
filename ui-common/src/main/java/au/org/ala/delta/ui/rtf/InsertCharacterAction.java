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
package au.org.ala.delta.ui.rtf;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class InsertCharacterAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private RtfEditor _editor;
	private char _char;

	public InsertCharacterAction(RtfEditor editor, char ch) {
		_editor = editor;
		_char = ch;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_editor.insertCharAtCaret(_char);
	}
}
