package au.org.ala.delta.editor.ui.validator;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.Attribute.AttributeParseException;
import au.org.ala.delta.model.Attribute;

/**
 * Delegates to the model classes to validate an attribute of an item.
 */
public class AttributeValidator implements Validator {

	/** The attribute we are validating the text for */
	private Attribute _attribute;	
	private EditorViewModel _model;
	
	/**
	 * Creates an AttributeValidator capable of validating an attribute identified by the
	 * supplied Item and Character.
	 * 
	 * @param dataset The dataset the attribute belongs to
	 * @param attribute The attribute the text will be validated on behalf of.
	 */
	public AttributeValidator(EditorViewModel dataset, Attribute attribute) {
		_attribute = attribute;
		_model = dataset;
	}
	
	/**
	 * Delegates to the model classes to perform the validation and converts an Exception into
	 * an appropriate message.
	 */
	public ValidationResult validate(Object attributeText) {
		ValidationResult result = null;
		
		String text = _model.attributeValueFromDisplayText(_attribute, (String)attributeText);
		
		try { 
			_attribute.getCharacter().validateAttributeText(text);
			result = new ValidationResult();
		} catch (AttributeParseException e) {
			result = new ValidationResult(e.getMessage(), e.getValue());
		}
		
		return result;
	}
}
