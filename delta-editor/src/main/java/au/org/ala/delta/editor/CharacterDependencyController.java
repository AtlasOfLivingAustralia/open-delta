package au.org.ala.delta.editor;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.util.MessageDialogHelper;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterDependencyFormatter;

/**
 * Responsible for making changes to CharacterDependencies.
 *
 */
public class CharacterDependencyController {

	private EditorViewModel _model;
	private MessageDialogHelper _messageDialogHelper;
	private CharacterDependencyFormatter _formatter;
	
	public CharacterDependencyController(EditorViewModel model) {
		_model = model;
		_messageDialogHelper = new MessageDialogHelper();
		_formatter = new CharacterDependencyFormatter(_model);
	}
	
	public void defineCharacterDependency(MultiStateCharacter controllingCharacter, Set<Integer> states) {
		// Validate first.
		
		_model.addCharacterDependency(controllingCharacter, states, new HashSet<Integer>());
	}
	
	public void redefineCharacterDependency(CharacterDependency characterDependency, Set<Integer> states) {
		
		if (states.isEmpty()) {
			deleteCharacterDependency(characterDependency);
			return;
		}
		
		
	}
	
	/**
	 * Asks for confirmation before deleting the supplied CharacterDependency
	 * from the data set.
	 * @param characterDependency the CharacterDependency to delete.
	 */
	public void deleteCharacterDependency(CharacterDependency characterDependency) {
		
		String description = _formatter.formatCharacterDependency(characterDependency);
		boolean delete = _messageDialogHelper.confirmDeleteCharacterDependency(description);
		if (delete) {
			_model.deleteCharacterDependency(characterDependency);
		}
	}
}
