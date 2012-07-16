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
package au.org.ala.delta.intkey.directives;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.DisplayCharacterOrderSeparateDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class DisplayCharacterOrderSeparateDirective extends IntkeyDirective {

    public DisplayCharacterOrderSeparateDirective() {
        super(true, "display", "characterorder", "separate");
    }

    @Override
    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        Item taxonToSeparate;

        if (data == null) {
            List<Item> selectedTaxonInList = context.getDirectivePopulator().promptForTaxaByList(StringUtils.join(getControlWords(), " ").toUpperCase(), true, true, true, false, null, null);
            if (selectedTaxonInList == null || selectedTaxonInList.size() == 0) {
                // cancel
                return null;
            } else {
                taxonToSeparate = selectedTaxonInList.get(0);
            }
        } else {
            boolean parseError = false;
            int taxonNumber = 0;
            int totalNumberOfTaxa = context.getDataset().getNumberOfTaxa();
            try {
                taxonNumber = Integer.parseInt(data);

                if (taxonNumber < 1 || taxonNumber > totalNumberOfTaxa) {
                    parseError = true;
                }
            } catch (NumberFormatException ex) {
                parseError = true;
            }

            if (parseError) {
                throw new IntkeyDirectiveParseException("InvalidTaxonNumber.error", context.getDataset().getNumberOfTaxa());
            }

            taxonToSeparate = context.getDataset().getItem(taxonNumber);
        }

        DisplayCharacterOrderSeparateDirectiveInvocation invoc = new DisplayCharacterOrderSeparateDirectiveInvocation(taxonToSeparate);

        return invoc;
    }
}
