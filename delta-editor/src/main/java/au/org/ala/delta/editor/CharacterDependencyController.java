package au.org.ala.delta.editor;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.util.MessageDialogHelper;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CircularDependencyException;
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
	
	/**
	 * Creates a new CharacterDependency.
	 * @param controllingCharacter the controlling character.
	 * @param states the set of states for the new CharacterDependency.
	 */
	public void defineCharacterDependency(MultiStateCharacter controllingCharacter, Set<Integer> states) {
		
		String defaultLabel = _formatter.defaultLabelFor(controllingCharacter, states);
		String label = _messageDialogHelper.promptForControllingAttributeLabel(defaultLabel);
		if (label != null) {
			CharacterDependency dependency = _model.addCharacterDependency(controllingCharacter, states, new HashSet<Integer>());
			dependency.setDescription(label);
		}
	}

	/**
	 * Updates the states that make up the supplied Character dependency.
	 * @param characterDependency the CharacterDependency to update
	 * @param states the new set of states.
	 */
	public void redefineCharacterDependency(CharacterDependency characterDependency, Set<Integer> states) {
		
		if (states.isEmpty()) {
			deleteCharacterDependency(characterDependency);
			return;
		}
		// TODO validate first.
		characterDependency.setStates(states);
	}
	
	public void addDependentCharacter(CharacterDependency characterDependency, au.org.ala.delta.model.Character dependent) {
		try {
			characterDependency.addDependentCharacter(dependent);
		}
		catch (CircularDependencyException e) {
			_messageDialogHelper.displayCircularDependencyError();
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
