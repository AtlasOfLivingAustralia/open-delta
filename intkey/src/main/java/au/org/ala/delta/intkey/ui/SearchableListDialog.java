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
package au.org.ala.delta.intkey.ui;

/** 
 * A dialog that has a "Search" button that is used in conjunction with SimpleSearchDialog
 * @author ChrisF
 *
 */
public interface SearchableListDialog {
    /**
     * Starting at the supplied index, search for the supplied text in the dialog, and select the first match.
     * @param searchText The text to search for
     * @param startingIndex the list index to begin searching from
     * @return the index of the first matching list item, or -1 if no match was found
     */
     int searchForText(String searchText, int startingIndex);
}
