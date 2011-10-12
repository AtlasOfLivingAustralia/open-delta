package au.org.ala.delta.intkey.model;

import java.io.File;
import java.io.IOException;
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
    public List<Character> promptForCharactersByKeyword(String directiveName, boolean permitSelectionFromIncludedCharactersOnly, boolean noneKeywordAvailable) {
        return null;
    }

    @Override
    public List<Character> promptForCharactersByList(String directiveName, boolean permitSelectionFromIncludedCharactersOnly) {
        return null;
    }

    @Override
    public List<Item> promptForTaxaByKeyword(String directiveName, boolean selectFromIncludedTaxaOnly, boolean noneKeywordAvailable) {
        return null;
    }

    @Override
    public List<Item> promptForTaxaByList(String directiveName, boolean selectFromIncludedTaxaOnly, boolean autoSelectSingleValue, boolean singleSelect) {
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

    @Override
    public File promptForFile(List<String> fileExtensions, String description, boolean createFileIfNonExistant) throws IOException {
        return null;
    }

    @Override
    public Boolean promptForOnOffValue(String directiveName, boolean initialValue) {
        return null;
    }

    @Override
    public List<Object> promptForMatchSettings() {
        return null;
    }

}
