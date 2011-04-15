package au.org.ala.delta.model.format;

import org.apache.commons.lang.StringUtils;

/**
 * Formats an attribute.
 */
public class AttributeFormatter extends CharacterFormatter {

	
	public AttributeFormatter(boolean includeNumber, boolean stripFormatting) {
		super(includeNumber, false, false, stripFormatting);
	}

	public AttributeFormatter(boolean includeNumber, boolean stripFormatting, boolean replaceBrackets) {
		super(includeNumber, false, replaceBrackets, stripFormatting);
	}
	
	/**
	 * Attribute formatting differs from Character and Item formatting in that by default
	 * attribute comments are not removed.
	 * @param attribute the attribute to format.
	 * @return the formatted attribute value.
	 */
	public String formatComment(String comment) {
		
		if (StringUtils.isEmpty(comment) || EMPTY_COMMENT_PATTERN.matcher(comment).matches()) {
			return "";
		}
		return defaultFormat(comment);
	}
	
}
