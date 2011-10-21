package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;

public class TaxonListArgument extends AbstractTaxonListArgument<List<Item>> {

    /**
     * @param name
     *            Name of the argument
     * @param promptText
     *            Text used to prompt the user for a value
     * @param defaultSelectionMode
     *            default selection mode when user is prompted for a value
     * @param selectFromAll
     *            When prompting, allow selection from all
     */
    public TaxonListArgument(String name, String promptText, SelectionMode defaultSelectionMode, boolean selectFromAll, boolean noneSelectionPermitted) {
        super(name, promptText, defaultSelectionMode, selectFromAll, noneSelectionPermitted);
    }

    @Override
    public List<Item> parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        boolean overrideExcludedTaxa = false;

        String token = inputTokens.poll();
        if (token != null && token.equalsIgnoreCase(OVERRIDE_EXCLUDED_TAXA)) {
            overrideExcludedTaxa = true;
            token = inputTokens.poll();
        }

        overrideExcludedTaxa = overrideExcludedTaxa || _selectFromAll;

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

                while (token != null) {
                    try {
                        taxa.addAll(ParsingUtils.parseTaxonToken(token, context));
                        token = inputTokens.poll();

                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(String.format("Unrecognized taxon keyword %s", token), ex);
                    }
                }
            }
            
            if (!overrideExcludedTaxa) {
                taxa.retainAll(context.getIncludedTaxa());
            }
        }

        if (taxa == null) {
            DirectivePopulator populator = context.getDirectivePopulator();
            if (selectionMode == SelectionMode.KEYWORD) {
                taxa = populator.promptForTaxaByKeyword(directiveName, !overrideExcludedTaxa, _noneSelectionPermitted);
            } else {
                boolean autoSelectSingleValue = (selectionMode == SelectionMode.LIST_AUTOSELECT_SINGLE_VALUE);
                taxa = populator.promptForTaxaByList(directiveName, !overrideExcludedTaxa, autoSelectSingleValue, false);
            }
        }

        stringRepresentationBuilder.append(" ");
        for (int i = 0; i < taxa.size(); i++) {
            Item taxon = taxa.get(i);
            stringRepresentationBuilder.append(taxon.getItemNumber());
            if (i < taxa.size() - 1) {
                stringRepresentationBuilder.append(" ");
            }
        }

        if (taxa.size() == 0 && !_noneSelectionPermitted) {
            throw new IntkeyDirectiveParseException("NoTaxaInSet.error");
        }

        Collections.sort(taxa);

        return taxa;
    }

}
