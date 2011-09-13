package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class CharacterListArgument extends IntkeyDirectiveArgument<List<au.org.ala.delta.model.Character>> {

    private static final String OVERRIDE_EXCLUDED_CHARACTERS = "/C";

    private SelectionMode _defaultSelectionMode;
    private boolean _selectFromAll;

    public CharacterListArgument(String name, String promptText, SelectionMode defaultSelectionMode, boolean selectFromAll) {
        super(name, promptText, null);
        _defaultSelectionMode = defaultSelectionMode;
        _selectFromAll = selectFromAll;
    }

    @Override
    public List<au.org.ala.delta.model.Character> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException {
        boolean overrideExcludedCharacters = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_CHARACTERS)) {
            overrideExcludedCharacters = true;
            token = inputTokens.poll();
        }

        List<au.org.ala.delta.model.Character> characters = null;

        SelectionMode selectionMode = _defaultSelectionMode;
        DirectivePopulator populator = context.getDirectivePopulator();
        
        if (token != null) {
            if (token.equalsIgnoreCase(DEFAULT_DIALOG_WILDCARD)) {
                selectionMode =  _defaultSelectionMode;
            } else if (token.equalsIgnoreCase(KEYWORD_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.KEYWORD;
            } else if (token.equalsIgnoreCase(LIST_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.LIST;
            } else {
                characters = new ArrayList<au.org.ala.delta.model.Character>();
                while (token != null) {
                    try {
                        characters.addAll(ParsingUtils.parseCharacterToken(token, context));
                        token = inputTokens.poll();
                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(String.format("Unrecognized character keyword %s", token), ex);
                    }
                }
            }
        } 
        
        if (characters == null) {
            if (selectionMode == SelectionMode.KEYWORD) {
                characters = populator.promptForCharactersByKeyword(directiveName, !overrideExcludedCharacters);
            } else {
                characters = populator.promptForCharactersByList(directiveName, _selectFromAll, !overrideExcludedCharacters);
            }
        }
        
        // An empty list indicates that the user hit cancel when prompted to select characters
        if (characters.size() == 0) {
            characters = null;
        }
        
        return characters;
    }
}
