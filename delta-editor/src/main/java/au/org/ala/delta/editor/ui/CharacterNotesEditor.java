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
package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * Allows the user to add or edit notes for a Character.
 */
public class CharacterNotesEditor extends CharacterEditTab {

	private static final long serialVersionUID = 8286423277647757100L;

	private RtfEditor editor;
	
	public CharacterNotesEditor(RtfToolBar toolbar) {
		super(toolbar);
		createUI();
		addEventListeners();
	}
	
	private void createUI() {
		setName("CharacterNotesEditor");
		setLayout(new BorderLayout());
		editor = new RtfEditor();
		_toolbar.addEditor(editor);
		add(new JScrollPane(editor), BorderLayout.CENTER);
		JLabel characterNotesLabel = new JLabel();
		characterNotesLabel.setName("characterNotesLabel");
		add(characterNotesLabel, BorderLayout.NORTH);
	}
	
	private void addEventListeners() {
		editor.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				_character.setNotes(editor.getRtfTextBody());
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
	}
	
	/**
	 * Sets the Character for editing.
	 * @param character the Character to edit.
	 */
	public void bind(EditorViewModel model, Character character) {
		_model = model;
		_character = character;
		editor.setText(_character.getNotes());
	}
}
