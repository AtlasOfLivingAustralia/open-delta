package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public interface DirectivePopulator {
    
    List<au.org.ala.delta.model.Character> promptForCharactersByKeyword(String directiveName, boolean permitSelectionFromIncludedCharactersOnly);
    List<au.org.ala.delta.model.Character> promptForCharactersByList(String directiveName, boolean selectFromAll, boolean selectIncludedCharactersOnly);
    
    List<Item> promptForTaxaByKeyword(String directiveName, boolean permitSelectionFromIncludedTaxaOnly);
    List<au.org.ala.delta.model.Character> promptForTaxaByList(String directiveName, boolean selectFromAll, boolean selectIncludedCharactersOnly);
    
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
    
    File promptForFile(List<String> fileExtensions, String description, boolean createFileIfNonExistant) throws IOException;
    
    Boolean promptForOnOffValue(String directiveName, boolean initialValue);

}
