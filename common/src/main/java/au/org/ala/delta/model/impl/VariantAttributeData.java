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
package au.org.ala.delta.model.impl;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.attribute.DefaultParsedAttribute;
import au.org.ala.delta.model.attribute.ParsedAttribute;
import org.apache.commons.lang.StringUtils;

/**
 * Overrides DefaultAttributeData to return the data of the master Item in the case that this Attribute has
 * no coded value.
 */
public class VariantAttributeData extends DefaultAttributeData {

    private Item _masterItem;

    public VariantAttributeData(Item master, Character character) {
        super(character);
        _masterItem = master;
    }

    @Override
    public String getValueAsString() {
        String attributeValue = super.getValueAsString();
        if (StringUtils.isEmpty(attributeValue)) {
            Attribute masterAttribute = _masterItem.getAttribute(_character);
            if (masterAttribute == null) {
                return "";
            }
            return masterAttribute.getValueAsString();
        }
        return attributeValue;
    }

    @Override
    protected DefaultParsedAttribute defaultParsedAttribute() {
        if (StringUtils.isEmpty(super.getValueAsString())) {
            Attribute masterAttribute = _masterItem.getAttribute(_character);
            if (masterAttribute != null) {
                return (DefaultParsedAttribute)masterAttribute.parsedAttribute();
            }
        }
        return _parsedAttribute;
    }

    @Override
    public ParsedAttribute parsedAttribute() {
        if (isInherited()) {
            Attribute masterAttribute = _masterItem.getAttribute(_character);
            if (masterAttribute != null) {
                return masterAttribute.parsedAttribute();
            }
        }
        return _parsedAttribute;
    }

    @Override
    public boolean isInherited() {
        String attributeValue = super.getValueAsString();
        return StringUtils.isEmpty(attributeValue);
    }

}
