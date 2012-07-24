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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class DefineNamesDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private String _keyword;
    private List<Item> _taxa;

    public DefineNamesDirectiveInvocation(String keyword, List<Item> taxa) {
        _keyword = keyword;
        _taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> taxaNumbers = new HashSet<Integer>();

        for (Item taxon : _taxa) {
            taxaNumbers.add(taxon.getItemNumber());
        }

        try {
            context.setTaxaKeyword(_keyword, taxaNumbers);
        } catch (IllegalArgumentException ex) {
            context.getUI().displayErrorMessage(String.format("'%s' is a system keyword and cannot be redefined", _keyword));
            return false;
        }

        return true;
    }

}
