package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class TaxonListArgument extends IntkeyDirectiveArgument<List<Item>> {

    private static final String OVERRIDE_EXCLUDED_TAXA = "/T";

    private SelectionMode _defaultSelectionMode;
    private boolean _selectFromAll;

    public TaxonListArgument(String name, String promptText, SelectionMode defaultSelectionMode, boolean selectFromAll) {
        super(name, promptText, null);
        _defaultSelectionMode = defaultSelectionMode;
        _selectFromAll = selectFromAll;
    }

    @Override
    public List<Item> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException {
        boolean overrideExcludedCharacters = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_TAXA)) {
            overrideExcludedCharacters = true;
            token = inputTokens.poll();
        }

        List<Item> taxa = null;

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
                taxa = new ArrayList<Item>();
                while (token != null) {
                    try {
                        taxa.addAll(ParsingUtils.parseTaxonToken(token, context));
                        token = inputTokens.poll();
                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(String.format("Unrecognized taxon keyword %s", token), ex);
                    }
                }
            }
        } 
        
        if (taxa == null) {
            if (selectionMode == SelectionMode.KEYWORD) {
                taxa = populator.promptForTaxaByKeyword(directiveName, !overrideExcludedCharacters);
            } else {
                taxa = populator.promptForTaxaByList(directiveName, _selectFromAll, !overrideExcludedCharacters);
            }
        }

        return taxa;
    }

}
