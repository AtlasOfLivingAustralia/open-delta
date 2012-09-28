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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.util.Utils;

/**
 * Misc formatting utilities
 * 
 * @author ChrisF
 * 
 */
public class FormattingUtils {

    /**
     * Formats the values set for an integer character as a string. It is
     * assumed the values for the integer character have already been processed
     * such that they are in the range minimumValue - 1 to maximumValue + 1.
     * 
     * @param integerValues
     *            the values set for the integer character
     * @param minimumValue
     *            the character's minimum value
     * @param maximumValue
     *            the character's maximum value
     * @return the integer character's values formatted as a string.
     */
    public static String formatIntegerValuesAsString(Set<Integer> integerValues, int minimumValue, int maximumValue) {
        Set<Integer> valuesCopy = new HashSet<Integer>(integerValues);

        List<String> stringParts = new ArrayList<String>();

        if (integerValues.contains(minimumValue - 1)) {
            stringParts.add(Integer.toString(minimumValue - 1));
        }

        valuesCopy.remove(minimumValue - 1);
        valuesCopy.remove(maximumValue + 1);

        if (!valuesCopy.isEmpty()) {
            List<Integer> valuesCopyAsList = new ArrayList<Integer>(valuesCopy);
            Collections.sort(valuesCopyAsList);
            stringParts.add(Utils.formatIntegersAsListOfRanges(valuesCopyAsList, "/", "-"));
        }

        if (integerValues.contains(maximumValue + 1)) {
            stringParts.add(Integer.toString(maximumValue + 1));
        }

        return StringUtils.join(stringParts, "/");
    }
}
