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
package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;

public class DisplayCharacterOrderSeparateDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private Item _taxonToSeparate;
    private ItemFormatter _formatter;

    public DisplayCharacterOrderSeparateDirectiveInvocation(Item taxonToSeparate) {
        _taxonToSeparate = taxonToSeparate;
        _formatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, true);
    }

    @Override
    public boolean execute(IntkeyContext context) {
        if (!context.getAvailableTaxa().contains(_taxonToSeparate)) {
            context.getUI().displayErrorMessage(
                    MessageFormat.format(UIUtils.getResourceString("DisplayCharacterOrderSeparate.TaxonNoLongerInContentionMsg"), _formatter.formatItemDescription(_taxonToSeparate)));
            return false;
        }

        context.setCharacterOrderSeparate(_taxonToSeparate.getItemNumber());
        return true;
    }

    @Override
    public String toString() {
        return String.format("DISPLAY CHARACTERORDER SEPARATE %s", _taxonToSeparate.getItemNumber());
    }
}
