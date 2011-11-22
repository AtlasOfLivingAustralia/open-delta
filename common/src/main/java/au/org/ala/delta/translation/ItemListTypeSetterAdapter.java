package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * An empty implementation of the ItemListTypeSetter interface.
 */
public class ItemListTypeSetterAdapter implements ItemListTypeSetter {

	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {}

	@Override
	public void afterItem(Item item) {}

	@Override
	public void beforeAttribute(Attribute attribute) {}

	@Override
	public void afterAttribute(Attribute attribute) {}

	@Override
	public void afterLastItem() {}

	@Override
	public void beforeItemHeading() {}

	@Override
	public void afterItemHeading() {}

	@Override
	public void beforeItemName() {}

	@Override
	public void afterItemName() {}

	@Override
	public void newParagraph() {}

	@Override
	public String typeSetItemDescription(String description) {
		return description;
	}

	@Override
	public void beforeNewParagraphCharacter() {}

	@Override
	public String rangeSeparator() {
		return "";
	}

	@Override
	public void beforeCharacterDescription(Character character, Item item) {}

	@Override
	public void afterCharacterDescription(Character character, Item item) {}

	@Override
	public void beforeEmphasizedCharacter() {}

	@Override
	public void afterEmphasizedCharacter() {}

}
