/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;

public class DefineNamesDirective extends IntkeyDirective {

    private ItemFormatter _taxonFormatter;

    public DefineNamesDirective() {
        super(true, "define", "names");
        _taxonFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false, false);
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String keyword = null;
        List<String> names = new ArrayList<String>();

        // Need to prompt if data starts with a wildcard - don't bother
        // tokenizing
        if (!data.toUpperCase().startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            // Taxon names are separated by newlines or by commas
            List<String> tokens = new StrTokenizer(data, StrMatcher.charSetMatcher(new char[] { '\n', '\r', ',' })).getTokenList();

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
                
                //If first name begins with a wildcard, we need to prompt for names. Clear out the names list if this is the case.
                if (!names.isEmpty()) {
                    if (names.get(0).toUpperCase().startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
                        names.clear();
                    }
                }
            }
        }

        List<Item> taxa = new ArrayList<Item>();
        for (String taxonName : names) {
            Item taxon = context.getDataset().getTaxonByName(taxonName);
            if (taxon == null) {
                throw new IntkeyDirectiveParseException(UIUtils.getResourceString("InvalidTaxonName.error", taxonName));
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
            List<String> selectedKeywords = new ArrayList<String>(); // Not
                                                                     // used,
                                                                     // but
                                                                     // required
                                                                     // as an
                                                                     // argument
            taxa = context.getDirectivePopulator().promptForTaxaByList(directiveName, false, false, false, false, null, selectedKeywords);
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
