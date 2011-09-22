package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Pair;

public class BracketedTaxonListArgument extends AbstractTaxonListArgument<Pair<List<Item>, Boolean>> {

    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";

    public BracketedTaxonListArgument(String name, String promptText, SelectionMode defaultSelectionMode, boolean selectFromAll) {
        super(name, promptText, defaultSelectionMode, selectFromAll);
    }

    @Override
    public Pair<List<Item>, Boolean> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException {
        boolean overrideExcludedCharacters = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_TAXA)) {
            overrideExcludedCharacters = true;
            token = inputTokens.poll();
        }

        boolean includeSpecimen = false;
        List<Item> taxa = null;

        SelectionMode selectionMode = _defaultSelectionMode;

        if (token != null) {
            if (token.equalsIgnoreCase(DEFAULT_DIALOG_WILDCARD)) {
                selectionMode = _defaultSelectionMode;
            } else if (token.equalsIgnoreCase(KEYWORD_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.KEYWORD;
            } else if (token.equalsIgnoreCase(LIST_DIALOG_WILDCARD)) {
                selectionMode = SelectionMode.LIST;
            } else if (token.equalsIgnoreCase(LIST_DIALOG_AUTO_SELECT_SOLE_ITEM_WILDCARD)) {
                selectionMode = SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE;
            } else {
                taxa = new ArrayList<Item>();

                boolean inBrackets = false;

                if (token.equals(OPEN_BRACKET)) {
                    inBrackets = true;
                    token = inputTokens.poll();
                }

                while (token != null) {

                    if (token.equals(CLOSE_BRACKET)) {
                        break;
                    }

                    try {

                        if (token.equalsIgnoreCase(IntkeyContext.SPECIMEN_KEYWORD)) {
                            includeSpecimen = true;
                        } else {
                            taxa.addAll(ParsingUtils.parseTaxonToken(token, context));
                        }

                        // If we are expecting a bracketed list, but no brackets
                        // are present,
                        // only parse the first token
                        if (!inBrackets) {
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
            // TODO NEED TO BE ABLE TO INCLUDE OPTION TO SELECT SPECIMEN IN
            // THESE PROMPTS
            DirectivePopulator populator = context.getDirectivePopulator();
            if (selectionMode == SelectionMode.KEYWORD) {
                taxa = populator.promptForTaxaByKeyword(directiveName, !overrideExcludedCharacters);
            } else {
                boolean autoSelectSingleValue = (selectionMode == SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE);
                taxa = populator.promptForTaxaByList(directiveName, _selectFromAll, !overrideExcludedCharacters, autoSelectSingleValue);
            }
        }

        // No taxa selected or specimen selected is assumed to indicated that
        // the user hit cancel
        if (taxa.size() == 0 && includeSpecimen == false) {
            return null;
        }

        return new Pair<List<Item>, Boolean>(taxa, includeSpecimen);
    }
}
