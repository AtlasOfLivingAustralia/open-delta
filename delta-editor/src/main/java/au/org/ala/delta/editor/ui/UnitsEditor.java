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
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * Allows the user to add or edit the units of a NumericCharacter.
 */
public class UnitsEditor extends CharacterEditTab {

	private static final long serialVersionUID = 8286423277647757100L;

	public UnitsEditor(RtfToolBar toolbar) {
		super(toolbar);
		createUI();
		addEventListeners();
	}
	
	private void createUI() {
		setName("CharacterUnitsEditor");
		setLayout(new BorderLayout());
		editor = new RtfEditor();
		_toolbar.addEditor(editor);
		add(new JScrollPane(editor), BorderLayout.CENTER);
		JLabel characterNotesLabel = new JLabel();
		characterNotesLabel.setName("characterUnitsLabel");
		add(characterNotesLabel, BorderLayout.NORTH);
	}
	
	private void addEventListeners() {
		editor.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (_character != null) {
					getCharacter().setUnits(editor.getRtfTextBody());
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
	}
	
	/**
	 * Sets the Character for editing.
	 * @param character the Character to edit.
	 */
	public void bind(EditorViewModel _model, au.org.ala.delta.model.Character character) {
		
		if (character.getCharacterType().isNumeric()) {
			_character = character;
			
			editor.setText(getCharacter().getUnits());
		}
		else {
			_character = null;
			editor.setText("");
		}
		
	}
	
	private NumericCharacter<?> getCharacter() {
		return (NumericCharacter<?>)_character;
	}
}
