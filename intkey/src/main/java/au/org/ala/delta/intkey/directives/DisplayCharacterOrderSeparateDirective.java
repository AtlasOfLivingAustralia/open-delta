package au.org.ala.delta.intkey.directives;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.DisplayCharacterOrderSeparateDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;

public class DisplayCharacterOrderSeparateDirective extends IntkeyDirective {

    public DisplayCharacterOrderSeparateDirective() {
        super(true, "display", "characterorder", "separate");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        Item taxonToSeparate;

        if (data == null) {
            List<Item> selectedTaxonInList = context.getDirectivePopulator().promptForTaxaByList(StringUtils.join(getControlWords(), " ").toUpperCase(), true, true, true);
            if (selectedTaxonInList == null || selectedTaxonInList.size() == 0) {
                // cancel
                return null;
            } else {
                taxonToSeparate = selectedTaxonInList.get(0);
            }
        } else {
            boolean parseError = false;
            int taxonNumber = 0;
            int totalNumberOfTaxa = context.getDataset().getNumberOfTaxa();
            try {
                taxonNumber = Integer.parseInt(data);

                if (taxonNumber < 1 || taxonNumber > totalNumberOfTaxa) {
                    parseError = true;
                }
            } catch (NumberFormatException ex) {
                parseError = true;
            }

            if (parseError) {
                throw new IntkeyDirectiveParseException("InvalidTaxonNumber.error", context.getDataset().getNumberOfTaxa());
            }

            taxonToSeparate = context.getDataset().getItem(taxonNumber);
        }

        DisplayCharacterOrderSeparateDirectiveInvocation invoc = new DisplayCharacterOrderSeparateDirectiveInvocation(taxonToSeparate);

        return invoc;
    }
}
