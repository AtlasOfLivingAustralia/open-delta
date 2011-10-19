package au.org.ala.delta.intkey.directives;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import au.org.ala.delta.intkey.directives.invocation.DefineNamesDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

public class DefineNamesDirective extends IntkeyDirective {

    private ItemFormatter _taxonFormatter;

    public DefineNamesDirective() {
        super("define", "names");
        _taxonFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false, false);
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        // Taxon names are separated by newlines or by commas
        List<String> tokens = new StrTokenizer(data, StrMatcher.charSetMatcher(new char[] { '\n', '\r', ',' })).getTokenList();

        String keyword = null;
        List<String> names = new ArrayList<String>();

        if (!tokens.isEmpty()) {
            String firstToken = tokens.get(0);

            // The keyword (which may quoted) and first taxon name may be
            // separated by a space
            List<String> splitFirstToken = new StrTokenizer(firstToken, StrMatcher.charSetMatcher(new char[] { ' ' }), StrMatcher.quoteMatcher()).getTokenList();
            keyword = splitFirstToken.get(0);
            if (splitFirstToken.size() > 1) {
                names.add(StringUtils.join(splitFirstToken.subList(1, splitFirstToken.size()), " "));
            }

            for (int i = 1; i < tokens.size(); i++) {
                names.add(tokens.get(i).trim());
            }
        }

        List<Item> taxa = new ArrayList<Item>();
        for (String taxonName : names) {
            Item taxon = context.getDataset().getTaxonByName(taxonName);
            if (taxon == null) {
                context.getUI().displayErrorMessage(MessageFormat.format(UIUtils.getResourceString("InvalidTaxonName.error"), taxonName));
                return null;
            } else {
                taxa.add(taxon);
            }
        }

        String directiveName = StringUtils.join(getControlWords(), " ").toUpperCase();

        if (StringUtils.isEmpty(keyword)) {
            keyword = context.getDirectivePopulator().promptForString("Enter keyword", null, directiveName);
            if (keyword == null) {
                // cancelled
                return null;
            }
        }

        if (taxa.isEmpty()) {
            taxa = context.getDirectivePopulator().promptForTaxaByList(directiveName, false, false, false);
            if (taxa == null || taxa.isEmpty()) {
                // cancelled
                return null;
            }

            // extract taxon names for use in building string representation of
            // command
            for (Item taxon : taxa) {
                names.add(_taxonFormatter.formatItemDescription(taxon));
            }
        }

        DefineNamesDirectiveInvocation invoc = new DefineNamesDirectiveInvocation(keyword, taxa);
        invoc.setStringRepresentation(String.format("%s \"%s\" %s", getControlWordsAsString(), keyword, StringUtils.join(names, ", ")));

        return invoc;
    }
}
