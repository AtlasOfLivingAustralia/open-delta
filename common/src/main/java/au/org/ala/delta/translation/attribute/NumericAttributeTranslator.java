package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
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
	
	public NumericAttributeTranslator(NumericCharacter<?> character) {
		_character = character;
		_formatter = new CharacterFormatter(false, true, true);
	}
	
	@Override
	public String translateValue(String value) {
		return value;
	}

	@Override
	public String rangeSeparator() {
		return "-";
	}
	

	/**
	 * Overrides the parent method to append the characters units, if any, to the translation.
	 */
	@Override
	protected void values(Values values) {
		super.values(values);
		
		appendUnits();
	}
	
	
	private void appendUnits() {
		if (_character.hasUnits()) {
			String units = _formatter.formatUnits(_character);
			_translatedValue.append(" ").append(units);
		}
		
	}
}
