package au.org.ala.delta.translation.attribute;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.RTFUtils;


/**
 * The TextAttributeTranslator is responsible for translating TextCharacter attributes into 
 * natural language.
 */
public class TextAttributeTranslator extends AttributeTranslator {

	@Override
	public String translateValue(String value) {
		throw new RuntimeException("This should never have been called");
	}

	@Override
	public String rangeSeparator() {
		return "";
	}
	
	/**
	 * Overrides the parent method to omit the brackets surrounding the comment.
	 */
	public void characterComment(String comment) {
		if (StringUtils.isNotEmpty(comment)) {
			comment = RTFUtils.stripFormatting(comment);
			// Omit the brackets either side of the comment.
			_translatedValue.append(removeCommentBrackets(comment));
		}
	}
	
	private String removeCommentBrackets(String comment) {
		int numBrackets = 0;
		while (comment.charAt(numBrackets) == '<') {
			numBrackets++;
		}
		
		return comment.substring(numBrackets, comment.length()-numBrackets);
	}
	

}
