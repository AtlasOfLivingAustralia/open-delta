package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.rtf.RTFBuilder;

public class TaxaDirectiveInvocation implements IntkeyDirectiveInvocation {

    private List<Item> _taxa;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) {

        ItemFormatter taxonFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, false, false, false);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Item taxon : _taxa) {
            builder.appendText(taxonFormatter.formatItemDescription(taxon));
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Taxa");

        return true;
    }

}
