package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the implicit attributes section during a natural language
 * translation if the TRANSLATE IMPLICIT VALUES directive is used.
 */
public class ImplicitValuesTranslator extends NaturalLanguageTranslator {

	public ImplicitValuesTranslator(DeltaContext context, DataSetFilter filter, ItemListTypeSetter typeSetter,
			PrintFile printer, ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
			AttributeFormatter attributeFormatter) {
		super(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
		// TODO Auto-generated constructor stub
	}


	private String _itemSubHeading;
	private ItemListTypeSetter _typeSetter;
	private CharacterFormatter _formatter;
	
	 private void translateImplicitValues() {
	    	
	    	for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
	    		_itemSubHeading = _context.getItemSubheading(i);
	    	
	    		Character character = _dataSet.getCharacter(i);
	    		if (character.getCharacterType().isMultistate()) {
	    			MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
	    			if (multiStateChar.getUncodedImplicitState() > 0) {
	    				
	    				//AttributeTranslator translator = new MultiStateAttributeTranslator(character, characterFormatter, formatter);
	    				//translator.translate(attribute);
	    			}
	    		}
	    	}
	 }
	 
	 
	 private String translateImplicit(MultiStateCharacter multiStateChar) {
		 int implicit = multiStateChar.getUncodedImplicitState();
		 if (implicit > 0) {
			 String description = _formatter.formatCharacterDescription(multiStateChar);
			 String state = _formatter.formatState(multiStateChar, implicit);
		 }
		 return "";
	 }
		 
	 }
	 