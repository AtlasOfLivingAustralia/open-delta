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
