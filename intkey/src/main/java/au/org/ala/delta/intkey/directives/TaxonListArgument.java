package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class TaxonListArgument extends IntkeyDirectiveArgument<List<Item>> {

    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String OVERRIDE_EXCLUDED_TAXA = "/T";

    private SelectionMode _defaultSelectionMode;
    private boolean _selectFromAll;
    private boolean _bracketed;

    /**
     * @param name Name of the argument 
     * @param promptText Text used to prompt the user for a value
     * @param defaultSelectionMode default selection mode when user is prompted for a value
     * @param selectFromAll When prompting, allow selection from all 
     * @param bracketed if true, the taxon numbers/ranges/keywords must be inside brackets, unless there is only a single
     * character number/range/keyword
     */
    public TaxonListArgument(String name, String promptText, SelectionMode defaultSelectionMode, boolean selectFromAll, boolean bracketed) {
        super(name, promptText, null);
        _defaultSelectionMode = defaultSelectionMode;
        _selectFromAll = selectFromAll;
        _bracketed = bracketed;
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
            } else if (token.endsWith(LIST_DIALOG_AUTO_SELECT_SOLE_ITEM_WILDCARD)) {
                selectionMode = SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE;
                //TODO fully implement ?L1 wildcard
            } else {
                taxa = new ArrayList<Item>();
                
                boolean inBrackets = false;
                
                if (_bracketed && token.equals(OPEN_BRACKET)) {
                    inBrackets = true;
                    token = inputTokens.poll();
                }                
                
                while (token != null) {

                    if (_bracketed && token.equals(CLOSE_BRACKET)) {
                        break;
                    }
                        
                    try {
                        taxa.addAll(ParsingUtils.parseTaxonToken(token, context));
                        
                        //If we are expecting a bracketed list, but no brackets are present, 
                        //only parse the first token
                        if (_bracketed && !inBrackets) {
                            break;
                        }
                        
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
        
        // An empty list indicates that the user hit cancel when prompted to select taxa
        if (taxa.size() == 0) {
            taxa = null;
        }

        return taxa;
    }

}
