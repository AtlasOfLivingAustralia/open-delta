package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.ui.rtf.RtfEditor;

/**
 * Allows the user to add or edit the units of a NumericCharacter.
 */
public class UnitsEditor extends CharacterEditTab {

	private static final long serialVersionUID = 8286423277647757100L;

	public UnitsEditor() {
		createUI();
		addEventListeners();
	}
	
	private void createUI() {
		setName("CharacterUnitsEditor");
		setLayout(new BorderLayout());
		editor = new RtfEditor();
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
