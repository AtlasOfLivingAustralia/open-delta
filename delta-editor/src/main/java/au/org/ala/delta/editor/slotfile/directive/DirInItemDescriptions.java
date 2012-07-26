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
package au.org.ala.delta.editor.slotfile.directive;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ItemDescriptions;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.Item;
import org.apache.commons.lang.NotImplementedException;

/**
 * Extends the ItemDescriptions class to support replacing an existing Item.
 */
public class DirInItemDescriptions extends ItemDescriptions implements DirectiveFunctor {

    public DirInItemDescriptions() {
        super(true);
    }

    protected Item createItem(DeltaContext context, int itemNumber, String description) {
        Item item = context.getDataSet().itemForDescription(description);

        if (item == null) {
            item = context.getDataSet().addItem();
        }
        else {
            if (item.isVariant()) {
                context.addError(new DirectiveError(DirectiveError.Warning.CANNOT_MAKE_ITEM_NON_VARIANT, 0));
            }
        }

        item.setDescription(description);
        return item;
    }

    protected Item createVariantItem(DeltaContext context, int masterItemNumber, int itemNumber, String description) {

        Item item = context.getDataSet().itemForDescription(description);
        if (item == null) {
            item = context.getDataSet().addVariantItem(masterItemNumber, itemNumber);
        }
        else {
            if (!item.isVariant()) {
                context.addError(new DirectiveError(DirectiveError.Warning.CANNOT_MAKE_ITEM_VARIANT, 0));
            }
        }
        item.setDescription(description);
        return item;
    }

    protected void checkItemCount(DeltaContext context, int itemIndex, int position) throws DirectiveException {
        // We allow the number of items to exceed the context "maximum number of items".
    }

	@Override
	public void process(DirectiveInOutState state) {
		throw new NotImplementedException();
		
	}

}
