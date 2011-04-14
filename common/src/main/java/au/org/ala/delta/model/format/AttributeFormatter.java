package au.org.ala.delta.model.format;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * Formats an attribute.
 */
public class AttributeFormatter extends CharacterFormatter {

	
	public AttributeFormatter(boolean includeNumber, boolean stripComments, boolean stripFormatting) {
		super(includeNumber, stripComments, stripFormatting);
	}
	
	public String format(Attribute attribute) {

		if ((attribute == null) || StringUtils.isEmpty(attribute.getValue())) {
			return "";
		}

		return RTFUtils.stripFormatting(attribute.getValue());
	}
	
}
