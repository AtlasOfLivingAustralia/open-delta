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
package au.org.ala.delta.model.format;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * A drop in replacement for the ItemFormatter that should be used when
 * the CHARACTER FOR TAXON NAMES directive is used.
 */
public class SubstitutingItemFormatter extends ItemFormatter {

	private Character _characterForTaxonNames;
	
	public SubstitutingItemFormatter(Character characterForTaxonNames) {
        this(characterForTaxonNames, true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, false, true, false);
    }

    public SubstitutingItemFormatter(Character characterForTaxonNames, 
    		boolean includeNumber, CommentStrippingMode commentStrippingMode, 
    		AngleBracketHandlingMode angleBracketHandlingMode, boolean stripRtf, 
    		boolean useShortVariant, boolean capitaliseFirstWord) {
        super(includeNumber, commentStrippingMode, angleBracketHandlingMode, stripRtf, useShortVariant, capitaliseFirstWord);
       
        if (characterForTaxonNames == null) {
        	throw new IllegalArgumentException("The supplied character may not be null");
        }
        _characterForTaxonNames = characterForTaxonNames;
    }
    
    /**
     * Formats the supplied item in a standard way according to the parameters
     * supplied at construction time.  If an attribute for the Character
     * supplied as the CHARACTER FOR TAXON NAMES has been provided for the
     * item, it will be used as the description instead of the item 
     * description.
     * 
     * @param Item the item to format.
     * @return a String representing the supplied Item.
     */
    public String formatItemDescription(Item item) {

    	String description = null;
    	Attribute attribute = item.getAttribute(_characterForTaxonNames);
    	if (attribute != null && StringUtils.isNotBlank(attribute.getValueAsString())) {
    		description = attribute.getValueAsString();
    	}
    	else {
    		description = item.getDescription();
    	}
        return formatItemDescription(item, description, _commentStrippingMode);

    }
}
