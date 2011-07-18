package au.org.ala.delta.inkey.model;

import java.util.List;

import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class MockDirectivePopulator implements DirectivePopulator {

    @Override
    public List<Character> promptForCharacters() {
        return null;
    }

    @Override
    public List<Item> promptForTaxa() {
        return null;
    }

    @Override
    public boolean promptForYesNoOption(String message) {
        return false;
    }

    @Override
    public boolean promptForString(String message) {
        return false;
    }

}
