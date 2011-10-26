package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Utils;

public class StatusIncludeTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        List<Item> includedTaxa = context.getIncludedTaxa();
        List<Integer> includedTaxaNumbers = new ArrayList<Integer>();
        for (Item taxon : includedTaxa) {
            includedTaxaNumbers.add(taxon.getItemNumber());
        }

        String formattedTaxaNumbers = Utils.formatIntegersAsListOfRanges(includedTaxaNumbers);

        builder.setTextColor(Color.BLUE);
        builder.appendText(UIUtils.getResourceString("Status.IncludeTaxa.title"));
        builder.setTextColor(Color.BLACK);
        builder.appendText(UIUtils.getResourceString("Status.IncludeTaxa.content", includedTaxa.size(), formattedTaxaNumbers));

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), UIUtils.getResourceString("Status.title"));

        return true;
    }

}
