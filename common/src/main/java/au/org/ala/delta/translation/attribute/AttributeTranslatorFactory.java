package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.TypeSetterFactory;

public class AttributeTranslatorFactory {

	private DeltaContext _context;
	private CharacterFormatter _characterFormatter;
	private AttributeFormatter _attributeFormatter;
	private ItemListTypeSetter _typeSetter;
	
	public AttributeTranslatorFactory(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		_context = context;
		_attributeFormatter = formatterFactory.createAttributeFormatter();
		_characterFormatter = formatterFactory.createCharacterFormatter();
		_typeSetter = new TypeSetterFactory().createTypeSetter(_context, null);
	}
	
	public AttributeTranslatorFactory(
			DeltaContext context, 
			CharacterFormatter charFormatter, 
			AttributeFormatter attributeFormatter,
			ItemListTypeSetter typeSetter) {
		_characterFormatter = charFormatter;
		_context = context;
		_attributeFormatter = attributeFormatter;
		_typeSetter = typeSetter;
	}

	public AttributeTranslator translatorFor(Character character) {
		boolean omitOr = _context.isOrOmmitedForCharacter(character.getCharacterId());
		if (character instanceof MultiStateCharacter) {
			return new MultiStateAttributeTranslator(
					(MultiStateCharacter) character, 
					_characterFormatter,
					_attributeFormatter,
					omitOr);
		}
		if (character instanceof NumericCharacter<?>) {
			return new NumericAttributeTranslator(
					(NumericCharacter<?>) character, 
					_typeSetter,
					_attributeFormatter,
					_context.getOmitSpaceBeforeUnits(), 
					omitOr);
		}

		return new TextAttributeTranslator(_attributeFormatter, omitOr);
	}
}
