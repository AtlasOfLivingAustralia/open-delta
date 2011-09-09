package au.org.ala.delta.editor.ui.validator;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.RTFUtils;

/**
 * Validates an Item.
 */
public class ItemValidator extends DescriptionValidator implements Validator {

	private static final String NO_ITEM_DESCRIPTION = "EMPTY_ITEM_DESCRIPTION";

	/**
	 * Ensures that the item description is not empty.
	 */
	@Override
	public ValidationResult validate(Object toValidate) {

		String unformattedDescription = RTFUtils.stripFormatting((String) toValidate).trim();
		ValidationResult result = null;
		if (StringUtils.isEmpty(unformattedDescription)) {
			result = new ValidationResult(NO_ITEM_DESCRIPTION, 0);
		} else {
			result = validateComments(unformattedDescription);
		}

		return result;
	}

}
