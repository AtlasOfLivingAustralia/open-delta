package au.org.ala.delta.model.format;

import au.org.ala.delta.model.Attribute;

/**
 * Formats an attribute.
 */
public class AttributeFormatter extends CharacterFormatter {

	
	public AttributeFormatter(boolean includeNumber, boolean stripFormatting) {
		super(includeNumber, false, stripFormatting);
	}

	
	/**
	 * Attribute formatting differs from Character and Item formatting in that by default
	 * attribute comments are not removed.
	 * @param attribute the attribute to format.
	 * @return the formatted attribute value.
	 */
	public String format(Attribute attribute) {
		return defaultFormat(attribute.getValue());
	}
	
}
