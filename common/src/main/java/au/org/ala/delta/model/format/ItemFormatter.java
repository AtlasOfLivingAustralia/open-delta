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

import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.Words;
import org.apache.commons.lang.StringUtils;

/**
 * Knows how to format items in a standard way.
 */
public class ItemFormatter extends Formatter {

    private boolean _includeNumber;
    private String _variant;

    public ItemFormatter() {
        this(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, false, true, false);
    }

    public ItemFormatter(boolean includeNumber, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripRtf, boolean useShortVariant,
            boolean capitaliseFirstWord) {
        super(commentStrippingMode, angleBracketHandlingMode, stripRtf, capitaliseFirstWord);
        _includeNumber = includeNumber;

        if (useShortVariant) {
            _variant = "(+)";
        } else {
            _variant = Words.word(Words.Word.VARIANT);
        }
    }

    /**
     * Specifies the symbol to be used when outputting a variant Item.
     * @param variant the symbol or description to use to indicate an Item is a variant of another Item.
     */
    public void setVariantDescription(String variant) {
        _variant = variant;
    }


    /**
     * Formats the supplied item in a standard way according to the parameters
     * supplied at construction time.
     * 
     * @param item the item to format.
     * @return a String representing the supplied Item.
     */
    public String formatItemDescription(Item item) {

        return formatItemDescription(item, _commentStrippingMode);

    }

    /**
     * Formats the supplied item in a standard way according to the parameters
     * supplied at construction time.
     * 
     * @param item the item to format.
     * @param commentStrippingMode true if comments should be removed from the description.
     * @return a String representing the supplied Item.
     */
    public String formatItemDescription(Item item, CommentStrippingMode commentStrippingMode) {

        return formatItemDescription(item, item.getDescription(), commentStrippingMode);    
    }
    
    protected String formatItemDescription(Item item, String description, CommentStrippingMode commentStrippingMode) {
    	 StringBuilder builder = new StringBuilder();
         if (_includeNumber) {
             builder.append(item.getItemNumber()).append(". ");
         }
         if (item.isVariant() && StringUtils.isNotBlank(_variant)) {
             builder.append(_variant).append(" ");
         }
         
         description = defaultFormat(description, commentStrippingMode, _angleBracketHandlingMode, _stripFormatting, _capitaliseFirstWord);

         builder.append(description);
         return builder.toString();
    }
}
