package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.VariantItem;

public abstract class AbstractDataSetFilter implements DataSetFilter {

	/** Configuration for the translation */
	protected DeltaContext _context;
	
	public AbstractDataSetFilter() {
		super();
	}

	/**
	 * If the INSERT REDUNDANT VARIANT ATTRIBUTES directive has been given
	 * return true. If the OMIT REDUNDANT VARIANT ATTRIBUTES directive has been
	 * given return true only if: 1) The attribute has been coded. 2) The coded
	 * value is different to the value of the attribute in the master Item. If
	 * neither of these directives have been given, return true if the character
	 * has been added.
	 * 
	 * @return true if the attribute should be output.
	 */
	protected boolean outputVariantAttribute(VariantItem item, Character character) {
		
		
		Boolean omitRedundantVariantAttributes = _context.getOmitRedundantVariantAttributes();
		if (omitRedundantVariantAttributes == null) {
			if (item.isInherited(character) &&
			    (_context.isCharacterAdded(item.getItemNumber(), character.getCharacterId()) == false)) {
				// Don't output this attribute
				return false;
			}
		} else if (omitRedundantVariantAttributes == true) {
			
			if (item.isInherited(character) && _context.isCharacterAdded(item.getItemNumber(), character.getCharacterId()) == false) {
				// Don't output this attribute
				return false;
			}
			Attribute attribute = item.getAttribute(character);
			return !(attribute.getValueAsString().equals(item.getParentAttribute(character).getValueAsString()));
				
		}
		
		return true;
	}

	protected int isIncluded(Item item, Character character) {
		int result = 1;
		int characterNum = character.getCharacterId();
		if (_context.isExcluded(characterNum)) {
			result = 0;
			// if _context.isCharacterAdded(int item, int character) ||
			// _context.isEmphasized(int item, int character) {
			// result = 2;
			// }
		}
	
		return result;
	}

	protected boolean outputImplictValue(Attribute attribute) {
		if (isIncluded(attribute.getItem(), attribute.getCharacter()) == 1) {
		    
			TranslateType type = _context.getTranslateType();
			if (type == TranslateType.NaturalLanguage || type == TranslateType.Delta) {
				return _context.getInsertImplicitValues();
			}
		}
		return true;
	}

}