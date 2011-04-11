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
	
	
	/**
	 * Returns the attribute of this Item for the specified character.  If this Item does
	 * not have a coded attribute, the parent's attribute will be returned.
	 * @param character the character to get the attribute for.
	 * @return the Attribute of this Item for the supplied Character.
	 */
	@Override
	public Attribute getAttribute(Character character) {
		Attribute attribute = null;
		if (isInherited(character)) {
			attribute = _parent.getAttribute(character);
		}
		else {
			attribute = super.getAttribute(character);
		}
		
		return attribute;
	}
	
	/**
	 * @return true if the attribute for the supplied character is inherited from the parent Item.
	 */
	public boolean isInherited(Character character) {
		Attribute attribute = super.getAttribute(character);
		return ((attribute == null) || StringUtils.isEmpty(attribute.getValue()));
	}


	public boolean isVariant() {
		return true;
	}
}
