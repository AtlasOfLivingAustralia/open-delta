package au.org.ala.delta.model;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.impl.ItemData;

/**
 * A variant Item represents a variant of another Item.
 * Any uncoded attributes of a variant Item are inherited from the Item's parent.
 *
 */
public class VariantItem extends Item {

	/** The Item to retrieve uncoded attributes from */
	private Item _parent;
	
	public VariantItem(Item parent, ItemData impl, int itemNumber) {
		super(impl, itemNumber);
		_parent = parent;
	}
	
		
	@Override
	protected Attribute doGetAttribute(Character character) {
		Attribute attribute = null;
		if (isInherited(character)) {
			attribute = getParentAttribute(character);
		}
		else {
			attribute = super.doGetAttribute(character);
		}
		return attribute;
	}
	
	/**
	 * @return true if the attribute for the supplied character is inherited from the parent Item.
	 */
	public boolean isInherited(Character character) {
		Attribute attribute = super.doGetAttribute(character);
		return ((attribute == null) || StringUtils.isEmpty(attribute.getValue()));
	}


	public boolean isVariant() {
		return true;
	}


	public Attribute getParentAttribute(Character character) {
		return _parent.doGetAttribute(character);
	}
}
