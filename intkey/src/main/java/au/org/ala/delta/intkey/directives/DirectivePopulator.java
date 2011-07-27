package au.org.ala.delta.intkey.directives;

import java.util.List;

import au.org.ala.delta.model.Item;

public interface DirectivePopulator {
    
    List<au.org.ala.delta.model.Character> promptForCharacters(String directiveName, boolean permitSelectionFromIncludedCharactersOnly);
    
    List<Item> promptForTaxa(String directiveName, boolean permitSelectionFromIncludedTaxaOnly);
    
    /**
     * Null denotes cancellation
     * @param message
     * @return
     */
    Boolean promptForYesNoOption(String message);
    
    String promptForString(String message, String initialSelectionValue, String directiveName);
    

}
