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
package au.org.ala.delta.editor.ui.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * Validates changes to Characters.
 */
public class CharacterValidator extends DescriptionValidator {

	private Character _character;
	private EditorViewModel _model;
	
	/**
	 * Creates an instance of the CharacterValidator specifically to validate the 
	 * supplied Character in the context of the supplied model.
	 * @param model the model the Character is a part of.
	 * @param character the Character to validate.
	 */
	public CharacterValidator(EditorViewModel model, Character character) {
		_character = character;
		_model = model;
	}
	
	public ValidationResult validateDescription(String description) {

		String plainText =  RTFUtils.stripFormatting((String) description).trim();
		if (StringUtils.isBlank(plainText)) {
			return new ValidationResult("empty.character.description", 0);
		} 
		return validateComments(description);
	}
	
	/**
	 * Changing the type of a Character is a major operation except for new Characters.
	 * @see au.gov.ala.delta.model.AbstractObservableDataSet.canChangeCharacterType
	 * @param type the new type for the Character.
	 * @return the result of the validation.
	 */
	public ValidationResult validateCharacterType(CharacterType type) {
		if (!_model.canChangeCharacterType(_character, type)) {
			return ValidationResult.error("cannot.change.type");
		}
		
		if (_model.getUncodedItems(_character).size() != _model.getMaximumNumberOfItems()) {
			return ValidationResult.warning("coded.data.exists");
		}
		
		return ValidationResult.success(); 
	}
	
	/**
	 * A change to the mandatory property is always allowed, however a warning will
	 * be issued if the Character is being made mandatory and uncoded attributes exist
	 * for the Character.
	 * @param mandatory the new value of the mandatory property.
	 * @return the results of the validation operation.
	 */
	public ValidationResult validateMandatory(boolean mandatory) {
		// check for and give warnings for uncoded mandatory characters.
		if (mandatory && !_character.isMandatory()) {
			List<Item> uncodedItems = _model.getUncodedItems(_character);
			if (uncodedItems.size() > 0) {
				return ValidationResult.warning("uncoded.mandatory.items.exist");
			}
		}
		return ValidationResult.success();
	}
	
	/**
	 * Validates a change of the exclusive property of the Character.
	 * Characters can always be made non-exclusive.
	 * Characters can only be made exclusive if there are no attributes coded
	 * against the Character with more than one state.
	 * 
	 * @param exclusive the new value for the exclusive property.
	 * @return a ValidationResult containing the outcome of the validation.
	 */
	public ValidationResult validateExclusive(boolean exclusive) {
		
		if (!_character.getCharacterType().isMultistate()) {
			return ValidationResult.error("only.multistate.chars.exclusive");
		}
		
		MultiStateCharacter multiStateChar = (MultiStateCharacter)_character;
		if (!multiStateChar.isExclusive()) {
			// We are making the Character exclusive, need to check if there are any
			// attributes coded with more than one state.
			if (_model.getItemsWithMultipleStatesCoded(multiStateChar).size() > 0) {
				return ValidationResult.error("items.with.multiple.states.encoded");
			}
		}
		return ValidationResult.success();
	}
	
	public ValidationResult validateStates() {
		if (_character.getCharacterType().isMultistate()) {
			if (((MultiStateCharacter)_character).getNumberOfStates() == 0) {
				return ValidationResult.error("MULTISTATE_CHARS_NEED_AT_LEAST_ONE_STATE");
			}
		
		}
		return ValidationResult.success();
	}
	
	/**
	 * Provides a Validator instance that will validate the Character feature description.
	 * @param model the model the Character to validate is a part of.
	 * @param character the Character to validate.
	 * @return an instance of the Validator class that can be used with the TextComponentValidator.
	 */
	public static Validator descriptionValidator(final EditorViewModel model, final Character character) {
		return new Validator() {
			
			@Override
			public ValidationResult validate(Object toValidate) {
				CharacterValidator validator = new CharacterValidator(model, character);
				return validator.validateDescription((String)toValidate);
			}
		};
	}
	
}
