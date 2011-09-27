package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.TypeSetterFactory;

public class AttributeTranslatorFactory {

	private DeltaContext _context;
	private FormatterFactory _formatterFactory;
	
	public AttributeTranslatorFactory(DeltaContext context) {
		_formatterFactory = new FormatterFactory(context);
		
	}

	public AttributeTranslator translatorFor(Character character) {
		if (character instanceof MultiStateCharacter) {
			return new MultiStateAttributeTranslator(
					(MultiStateCharacter) character, 
					_formatterFactory.createCharacterFormatter(),
					_formatterFactory.createAttributeFormatter());
		}
		if (character instanceof NumericCharacter<?>) {
			return new NumericAttributeTranslator(
					(NumericCharacter<?>) character, 
					new TypeSetterFactory().createTypeSetter(_context, null),
					_formatterFactory.createAttributeFormatter(),
					_context.getOmitSpaceBeforeUnits());
		}

		return new TextAttributeTranslator(_formatterFactory.createAttributeFormatter());
	}
}
