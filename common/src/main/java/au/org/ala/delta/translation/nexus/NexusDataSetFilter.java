package au.org.ala.delta.translation.nexus;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.AbstractDataSetFilter;

public class NexusDataSetFilter extends AbstractDataSetFilter {

	
	public NexusDataSetFilter(DeltaContext context) {
		_context = context;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}

	
	
	@Override
	public boolean filter(Item item, Character character) {
		
		return filter(character);
	}

	/**
	 * Text Characters and Numeric Characters without a Key State defined
	 * are excluded from Nexus transformations.
	 */
	@Override
	public boolean filter(Character character) {
		if (!_context.isCharacterExcluded(character.getCharacterId())) {
			CharacterType type = character.getCharacterType();
			if (type.isMultistate()) {
				return true;
			}
			else if (type.isNumeric()) {
				IdentificationKeyCharacter idChar = _context.getIdentificationKeyCharacter(character.getCharacterId());
				if (idChar.getNumberOfStates() > 0) {
					return true;
				}
			}
		}
		return false;
	}

}
