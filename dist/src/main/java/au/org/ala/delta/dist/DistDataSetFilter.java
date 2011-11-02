package au.org.ala.delta.dist;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.DataSetFilter;

/**
 * Filters exclusively on whether the Item or Character has been excluded
 * via include/exclude/Item/Character calls on the DistContext.
 *
 */
public class DistDataSetFilter implements DataSetFilter {

	private DistContext _context;
	
	public DistDataSetFilter(DistContext context) {
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
