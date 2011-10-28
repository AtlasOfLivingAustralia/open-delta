package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.VariantItem;
import au.org.ala.delta.translation.AbstractDataSetFilter;
import au.org.ala.delta.translation.DataSetFilter;

/**
 * The DataSetFilter is responsible for determining whether elements of a DeltaDataSet
 * should be included in a translation operation.
 */
public class UncodedCharactersFilter extends AbstractDataSetFilter implements DataSetFilter {

	/**
	 * Creates a new DataSetFilter
	 * @param context
	 */
	public UncodedCharactersFilter(DeltaContext context) {
		_context = context;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}
	
	
	@Override
	public boolean filter(Item item, Character character) {
		
		if (!filter(character)) {
			return false;
		}
		
		Attribute attribute = item.getAttribute(character);

		if (item.isVariant()) {
			return outputVariantAttribute((VariantItem)item, character);
		}
		
		if (attribute instanceof MultiStateAttribute && ((MultiStateAttribute)attribute).isImplicit()) {
			return outputImplictValue(attribute);
		}
		
		return true;
	}
	
	@Override
	public boolean filter(Character character) {
		try {
			return !_context.isCharacterExcluded(character.getCharacterId());
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
