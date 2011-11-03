package au.org.ala.delta.editor.ui.validator;

import au.org.ala.delta.editor.slotfile.Attribute.AttributeParseException;
import au.org.ala.delta.model.MutableDeltaDataSet;

/**
 * Delegates to the model classes to validate an attribute of an item.
 */
public class AttributeValidator implements Validator {

	/** The character the attribute is a value for */
	private au.org.ala.delta.model.Character _character;	
	
	/**
	 * Creates an AttributeValidator capable of validating an attribute identified by the
	 * supplied Item and Character.
	 * 
	 * @param item The Item the attribute belongs to
	 * @param character The character the attribute is a value for 
	 */
	public AttributeValidator(MutableDeltaDataSet dataset, au.org.ala.delta.model.Character character) {
		_character = character;
	}
	
	/**
	 * Delegates to the model classes to perform the validation and converts an Exception into
	 * an appropriate message.
	 */
	public ValidationResult validate(Object attributeText) {
		ValidationResult result = null;
		
		try { 
			_character.validateAttributeText((String) attributeText);
			result = new ValidationResult();
		} catch (AttributeParseException e) {
			result = new ValidationResult(e.getMessage(), e.getValue());
		}
		
		return result;
	}
}
