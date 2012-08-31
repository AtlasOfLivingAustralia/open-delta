/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.model;

import au.org.ala.delta.model.impl.ItemData;

/**
 * A variant Item represents a variant of another Item.
 * Any uncoded attributes of a variant Item are inherited from the Item's parent.
 *
 */
public class VariantItem extends Item {

	/** The Item to retrieve uncoded attributes from */
	private Item _parent;
	
	public VariantItem(Item parent, ItemData impl) {
		super(impl);
		_parent = parent;
	}

	/**
	 * @return true if the attribute for the supplied character is inherited from the parent Item.
	 */
	public boolean isInherited(Character character) {
		Attribute attribute = doGetAttribute(character);

		return ((attribute == null) || attribute.isInherited());
	}


	public boolean isVariant() {
		return true;
	}


	public Attribute getParentAttribute(Character character) {

        return _parent.doGetAttribute(character);
	}

    public Item getMasterItem() {
        return _parent;
    }
}
