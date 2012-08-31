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
package au.org.ala.delta.model.attribute;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Extends the DefaultAttributeChunkFormatter to format numbers to a specified number of decimal places.
 */
public class DecimalPlacesAttributeChunkFormatter extends DefaultAttributeChunkFormatter {

    private int _decimalPlaces;

    public DecimalPlacesAttributeChunkFormatter(boolean encloseInCommentBrackets, String rangeSeparator, int decimalPlaces) {
        super(encloseInCommentBrackets, rangeSeparator);
        _decimalPlaces = decimalPlaces;
    }

    /**
     * Overrides formatNumber in the parent class to format the number to 5 significant figures.  Trailing
     * zeros are stripped.
     * Note: for compatibility with the original CONFOR significant figures are only applied to values after
     * the decimal place. (e.g. 123456.7 will be formatted as 123456, not 123460)
     * @param number the number to format.
     * @return the supplied number as a String.
     */
    @Override
    public String formatNumber(BigDecimal number) {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(_decimalPlaces);
        format.setMaximumFractionDigits(_decimalPlaces);
        format.setMinimumIntegerDigits(1);
        format.setGroupingUsed(false);
        return format.format(number);

    }
}
