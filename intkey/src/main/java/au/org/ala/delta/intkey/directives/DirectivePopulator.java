package au.org.ala.delta.intkey.directives;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

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
    
    List<String> promptForTextValue(TextCharacter ch);
    
    Set<Integer> promptForIntegerValue(IntegerCharacter ch);
    
    FloatRange promptForRealValue(RealCharacter ch);
    
    Set<Integer> promptForMultiStateValue(MultiStateCharacter ch);

}
