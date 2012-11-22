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
package au.org.ala.delta.intkey.model;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.impl.DefaultItemData;

import java.util.List;

/**
 * Intkey implementation of ItemData. Makes getAttributes(), getAttribute() and
 * addAttribute() unsupported operations because due to memory restrictions,
 * this data needs to be read straight from disk in Intkey.
 * 
 * @author ChrisF
 * 
 */
public class IntkeyItemData extends DefaultItemData {

    // These methods are not supported as to get attribute data,
    // Intkey has to read this data off disk using methods provided
    // in the IntkeyDataset class.

    public IntkeyItemData(int number) {
        super(number, null);
    }

    @Override
    public List<Attribute> getAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attribute getAttribute(Character character, Item item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAttribute(Character character, Attribute attribute) {
        throw new UnsupportedOperationException();
    }

}
