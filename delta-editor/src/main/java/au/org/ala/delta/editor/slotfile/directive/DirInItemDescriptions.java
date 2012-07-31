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
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.DuplicateItemWarningDialog;
import au.org.ala.delta.model.Item;
import org.apache.commons.lang.NotImplementedException;
import org.jdesktop.application.Application;

import javax.swing.*;

/**
 * Extends the ItemDescriptions class to support replacing an existing Item.
 */
public class DirInItemDescriptions extends ItemDescriptions implements DirectiveFunctor {

    private volatile boolean _alwaysOverwriteItem;
    private volatile boolean _alwaysRetainItem;
    private volatile boolean _overwrite;

    public DirInItemDescriptions() {
        super(true);
        _alwaysOverwriteItem = false;
        _alwaysRetainItem = false;
        _overwrite = true;
    }

    private void askWhatToDo(Item item) {
        final String description = item.getDescription();

        try {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                DuplicateItemWarningDialog dialog = new DuplicateItemWarningDialog(description);
                DeltaEditor editor = (DeltaEditor)Application.getInstance();
                dialog.pack();
                editor.show(dialog);
                _overwrite = dialog.getOverwriteItem();
                if (dialog.getApplyToAll()) {
                    if (_overwrite) {
                        _alwaysOverwriteItem = true;
                        _alwaysRetainItem = false;
                    }
                    else {
                        _alwaysOverwriteItem = false;
                        _alwaysRetainItem = true;
                    }
                }
                else {
                    _alwaysOverwriteItem = false;
                    _alwaysRetainItem = false;
                }
            }
        });
        }
        catch (Exception e) {
            _alwaysOverwriteItem = false;
            _alwaysRetainItem = false;
            _overwrite = false;
        }


    }

    protected Item createItem(DeltaContext context, int itemNumber, String description) {
        Item item = context.getDataSet().itemForDescription(description);

        if (item == null) {
            item = context.getDataSet().addItem();
            item.setDescription(description);
        }
        else {
            if (item.isVariant()) {
                context.addError(new DirectiveError(DirectiveError.Warning.CANNOT_MAKE_ITEM_NON_VARIANT, 0));
            }

            checkAndUpdateDescription(context, description, item);
        }


        return item;
    }

    private void checkAndUpdateDescription(DeltaContext context, String description, Item item) {
        if (_alwaysOverwriteItem) {
            item.setDescription(description);
        }
        else if (!_alwaysRetainItem) {
            askWhatToDo(item);
            if (_overwrite) {
                item.setDescription(description);
            }
            else {
                context.addError(new DirectiveError(DirectiveError.Warning.ITEM_EXISTS, 0, description));
            }
        }
        else {
            context.addError(new DirectiveError(DirectiveError.Warning.ITEM_EXISTS, 0, description));
        }
    }

    protected Item createVariantItem(DeltaContext context, int masterItemNumber, int itemNumber, String description) {

        Item item = context.getDataSet().itemForDescription(description);
        if (item == null) {
            item = context.getDataSet().addVariantItem(masterItemNumber, itemNumber);
            item.setDescription(description);
        }
        else {
            if (!item.isVariant()) {
                context.addError(new DirectiveError(DirectiveError.Warning.CANNOT_MAKE_ITEM_VARIANT, 0));
            }
            checkAndUpdateDescription(context, description, item);
        }

        return item;
    }

    @Override
    protected void addAttribute(DeltaContext context, Item item, int charIdx, int startOffset, String value) throws DirectiveException {
        if (_overwrite) {
            super.addAttribute(context, item, charIdx, startOffset, value);
        }
    }

    protected void checkItemCount(DeltaContext context, int itemIndex, int position) throws DirectiveException {
        // We allow the number of items to exceed the context "maximum number of items".
    }

    @Override
	public void process(DirectiveInOutState state) {
		throw new NotImplementedException();
		
	}

}
