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

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class IllustrateTaxaDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private List<Item> _taxa;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        //filter out any taxa that do not have images
        List<Item> taxaWithoutImages = new ArrayList<Item>();
        for (Item taxon: _taxa) {
            if (taxon.getImageCount() == 0) {
                taxaWithoutImages.add(taxon);
            }
        }
        
        _taxa.removeAll(taxaWithoutImages);
        
        if (_taxa.isEmpty()) {
            context.getUI().displayErrorMessage("No images for the specified taxa");
            return false;
        }
        
        context.getUI().illustrateTaxa(_taxa);
        return true;
    }

}
