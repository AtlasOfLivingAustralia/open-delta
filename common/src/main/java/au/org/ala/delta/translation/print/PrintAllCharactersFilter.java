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
package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.*;
import au.org.ala.delta.translation.delta.DeltaFormatDataSetFilter;

/**
 * Used as the filter for the PRINT CHARACTER LIST and PRINT ITEM DESCRIPTIONS PrintActions when the
 * PRINT ALL CHARACTERS directive is in force.
 */
public class PrintAllCharactersFilter extends DeltaFormatDataSetFilter {

    /**
     * Creates a new PrintAllCharactersFilter
     * @param context the DeltaContext the filter works from.
     */
    public PrintAllCharactersFilter(DeltaContext context) {
        super(context);
    }

    @Override
    public boolean filter(Item item) {
        return true;
    }

    @Override
    public boolean filter(au.org.ala.delta.model.Character character) {
        return true;
    }
}
