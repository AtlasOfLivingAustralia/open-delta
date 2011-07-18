package au.org.ala.delta.intkey.directives;

import java.util.List;

import au.org.ala.delta.model.Item;

public interface DirectivePopulator {
    
    List<au.org.ala.delta.model.Character> promptForCharacters(String directiveName);
    
    List<Item> promptForTaxa(String directiveName);
    
    boolean promptForYesNoOption(String message);
    
    boolean promptForString(String message);
}
