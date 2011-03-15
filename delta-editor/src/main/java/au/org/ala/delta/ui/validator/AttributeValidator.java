package au.org.ala.delta.ui.validator;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.slotfile.Attribute.AttributeParseException;

/**
 * Delegates to the model classes to validate an attribute of an item.
 */
public class AttributeValidator implements Validator {

	/** The Item the attribute belongs to */
	private Item _item;
	/** The character the attribute is a value for */
	private au.org.ala.delta.model.Character _character;
	
	/**
	 * Creates an AttributeValidator capable of validating an attribute identified by the
	 * supplied Item and Character.
	 * 
	 * @param item The Item the attribute belongs to
	 * @param character The character the attribute is a value for 
	 */
	public AttributeValidator(Item item, au.org.ala.delta.model.Character character) {
		_item = item;
		_character = character;
	}
	
	/**
	 * Delegates to the model classes to perform the validation and converts an Exception into
	 * an appropriate message.
	 */
	public ValidationResult validate(Object attributeText) {
		ValidationResult result = null;
		
		try {
			_item.getAttribute(_character).setValue((String)attributeText);
			result = new ValidationResult();
		}
		catch (AttributeParseException e) {
			result = new ValidationResult(e.getMessage(), e.getValue());
		}
		
		return result;
	}
}
