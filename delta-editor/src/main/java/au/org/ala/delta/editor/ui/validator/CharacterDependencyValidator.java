package au.org.ala.delta.editor.ui.validator;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.CharacterDependency;

public class CharacterDependencyValidator {

	private EditorViewModel _model;
	
	/**
	 * Creates an instance of the CharacterDependencyValidator to validate 
	 * CharacterDependencies in the context of the supplied model.
	 * @param model the model the CharacterDependency is a part of.
	 */
	public CharacterDependencyValidator(EditorViewModel model, CharacterDependency character) {
		_model = model;
	}
	
	public void validate(CharacterDependency states) {
		
	}
	
}
