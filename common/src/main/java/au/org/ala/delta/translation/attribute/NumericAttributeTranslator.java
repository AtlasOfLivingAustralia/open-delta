package au.org.ala.delta.translation.attribute;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.TypeSetter;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

/**
 * The NumericAttributeTranslator is responsible for translating NumericCharacter attributes into 
 * natural language.
 */
public class NumericAttributeTranslator extends AttributeTranslator {

	/** The character associated with attribute to translate */
	private NumericCharacter<?> _character;
	
	/** Knows how to format character units  */
	private CharacterFormatter _formatter;
	
	/** Knows how to type set a range symbol */
	private TypeSetter _typeSetter;
	
	public NumericAttributeTranslator(NumericCharacter<?> character, TypeSetter typeSetter) {
		_character = character;
		_formatter = new CharacterFormatter(false, true, false, true);
		_typeSetter = typeSetter;
	}
	
	@Override
	public String translateValue(String value) {
		return value;
	}

	@Override
	public String rangeSeparator() {
		return _typeSetter.rangeSeparator();
	}
	

	/**
	 * Overrides the parent method to append the characters units, if any, to the translation.
	 */
	@Override
	protected String values(Values values) {
		
		StringBuilder output = new StringBuilder();
		
		String value = super.values(values);
		if (StringUtils.isNotEmpty(value)) {
			output.append(value).append(getUnits());
		}
		return output.toString();
	}
	
	
	private String getUnits() {
		StringBuilder output = new StringBuilder();
		if (_character.hasUnits()) {
			String units = _formatter.formatUnits(_character);
			output.append(" ").append(units);
		}
		return output.toString();
	}
}
