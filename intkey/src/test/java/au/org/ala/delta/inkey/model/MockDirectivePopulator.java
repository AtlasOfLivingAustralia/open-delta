package au.org.ala.delta.inkey.model;

import java.util.List;

import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class MockDirectivePopulator implements DirectivePopulator {

    @Override
    public List<Character> promptForCharacters(String directiveName) {
        return null;
    }

    @Override
    public List<Item> promptForTaxa(String directiveName) {
        return null;
    }

    @Override
    public Boolean promptForYesNoOption(String message) {
        return false;
    }

    @Override
    public String promptForString(String message, String initialValue) {
        return null;
    }

}
