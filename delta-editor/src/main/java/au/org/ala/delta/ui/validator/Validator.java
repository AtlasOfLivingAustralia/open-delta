package au.org.ala.delta.ui.validator;

/**
 * Simple interface for validator classes to implement for use with the TextComponentValidator.
 */
public interface Validator {

	/**
	 * Validate the supplied object and return an instance of ValidationResult.
	 * @param toValidate the object to validate.
	 * @return results of the validation.
	 */
	public ValidationResult validate(Object toValidate);
}
