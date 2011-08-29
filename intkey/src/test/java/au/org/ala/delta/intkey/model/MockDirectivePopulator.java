package au.org.ala.delta.intkey.model;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class MockDirectivePopulator implements DirectivePopulator {

    @Override
    public List<Character> promptForCharacters(String directiveName, boolean permitSelectionFromIncludedCharactersOnly) {
        return null;
    }

    @Override
    public List<Item> promptForTaxa(String directiveName, boolean permitSelectionFromIncludedTaxaOnly) {
        return null;
    }

    @Override
    public Boolean promptForYesNoOption(String message) {
        return false;
    }

    @Override
    public String promptForString(String message, String initialValue, String directiveName) {
        return null;
    }

    @Override
    public List<String> promptForTextValue(TextCharacter ch) {
        return null;
    }

    @Override
    public Set<Integer> promptForIntegerValue(IntegerCharacter ch) {
        return null;
    }

    @Override
    public FloatRange promptForRealValue(RealCharacter ch) {
        return null;
    }

    @Override
    public Set<Integer> promptForMultiStateValue(MultiStateCharacter ch) {
        return null;
    }

}
