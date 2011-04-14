package au.org.ala.delta.translation.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.attribute.ParsedAttribute.CommentedValues;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

/**
 * Translates an Attribute into natural language.
 */
public abstract class AttributeTranslator {

	private Map<String, String> _separators;
	protected StringBuilder _translatedValue;
	
	public AttributeTranslator() {
		_separators = new HashMap<String, String>();
		_separators.put("&", "and");
		_separators.put("-", "to");
	}
	
	/**
	 * Translates the supplied ParsedAttribute into a natural language description of the attribute.
	 * @param attribute the attribute to translate.
	 * @return a String containing the natural language description of the attribute.
	 */
	public String translate(ParsedAttribute attribute) {
		_translatedValue = new StringBuilder();
		characterComment(attribute.getCharacterComment());
		
		List<CommentedValues> commentedValues = attribute.getCommentedValues();
		for (int i=0; i<commentedValues.size()-1; i++) {
			commentedValues(commentedValues.get(i));
			_translatedValue.append(", or ");
		}
		if (commentedValues.size() > 0) {
			commentedValues(commentedValues.get(commentedValues.size()-1));
		}
		return _translatedValue.toString().trim();
	}
	
	public void commentedValues(CommentedValues commentedValues) {
	
		
		// Special case - if an attribute is marked not applicable and has a comment
		// the "not applicable" is omitted from the description.
		if (!isInapplicableWithComment(commentedValues)) {
			values(commentedValues.getValues());
		}
		comment(commentedValues.getComment());
	}
	
	private boolean isInapplicableWithComment(CommentedValues commentedValues) {
		
		Values values = commentedValues.getValues();
		String comment = commentedValues.getComment();
		return(StringUtils.isNotEmpty(comment) && 
				values.getNumValues() == 1 && "-".equals(values.getValue(0)));
			
	}
	
	public void characterComment(String comment) {
		if (StringUtils.isNotEmpty(comment)) {
			_translatedValue.append(comment);
			_translatedValue.append(" ");
		}
	}
	
	public void comment(String comment) {
		if (StringUtils.isNotEmpty(comment)) {
			
			_translatedValue.append(" ");
			_translatedValue.append(RTFUtils.stripFormatting(comment));
		}
	}
	
	protected void values(Values values) {
		if (values == null) {
			return;
		}
		List<String> valueStrings = values.getValues();
		if (valueStrings.isEmpty()) {
			return;
		}
		_translatedValue.append(getTranslatedValue(valueStrings.get(0)));
		if ("&".equals(values.getSeparator())) {
			
			translateAndSeparatedValues(valueStrings);
		}
		if ("-".equals(values.getSeparator())) {
			translateOrSeparatedValues(valueStrings);
		}
		
	}

	protected void translateOrSeparatedValues(List<String> valueStrings) {
		for (int i=1; i<valueStrings.size(); i++) {
			
			_translatedValue.append(rangeSeparator());
			_translatedValue.append(getTranslatedValue(valueStrings.get(i)));
		}
	}

	protected void translateAndSeparatedValues(List<String> valueStrings) {
		for (int i=1; i<valueStrings.size(); i++) {
			if (valueStrings.size() > 2) {
				_translatedValue.append(",");
			}
			_translatedValue.append(" ");
			if (i == valueStrings.size()-1) {
				_translatedValue.append("and ");
			}
			_translatedValue.append(getTranslatedValue(valueStrings.get(i)));
		}
	}
	
	protected String getTranslatedValue(String value) {
		
		if ("V".equals(value)) {
			return "variable";
		}
		if ("U".equals(value)) {
			return "unknown";
		}
		if ("-".equals(value)) {
			return "not applicable";
		}
		return translateValue(value);
	}
	
	public abstract String rangeSeparator();
	
	public abstract String translateValue(String value);
	
}
