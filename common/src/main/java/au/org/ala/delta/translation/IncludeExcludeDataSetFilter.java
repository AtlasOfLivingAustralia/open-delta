package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Filters exclusively on whether the Item or Character has been excluded
 * via include/exclude/Item/Character calls on the DistContext.
 *
 */
public class IncludeExcludeDataSetFilter implements DataSetFilter {

	private DeltaContext _context;
	
	public IncludeExcludeDataSetFilter(DeltaContext context) {
		_context = context;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}
	

	@Override
	public boolean filter(Item item, Character character) {
		return true;
	}

	@Override
	public boolean filter(Character character) {
		return !_context.isCharacterExcluded(character.getCharacterId());
	}

}
