package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.model.format.AttributeFormatter;




/**
 * The TextAttributeTranslator is responsible for translating TextCharacter attributes into 
 * natural language.
 */
public class TextAttributeTranslator extends AttributeTranslator {

	public TextAttributeTranslator(AttributeFormatter formatter) {
		super(formatter);
	}
	
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
	public String characterComment(String comment) {
		
		comment = _attributeFormatter.formatComment(comment);
		return removeCommentBrackets(comment);
	}
	
	private String removeCommentBrackets(String comment) {
		int numBrackets = 0;
		while (comment.charAt(numBrackets) == '<') {
			numBrackets++;
		}
		
		return comment.substring(numBrackets, comment.length()-numBrackets);
	}
	

}
