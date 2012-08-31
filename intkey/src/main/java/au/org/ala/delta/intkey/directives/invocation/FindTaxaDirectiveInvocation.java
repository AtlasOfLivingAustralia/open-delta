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
package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;
import java.util.Map;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.SearchUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

public class FindTaxaDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private String _searchText;

    public void setSearchText(String searchText) {
        this._searchText = searchText;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        boolean displayNumbering = context.displayNumbering();
        ItemFormatter taxonFormatter = new ItemFormatter(displayNumbering, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, false);
        CharacterFormatter characterFormatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, false, false);
        AttributeFormatter attributeFormatter = new AttributeFormatter(displayNumbering, false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, false, context
                .getDataset().getOrWord());

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        Map<Item, List<TextAttribute>> taxaSynonymyAttributes = context.getDataset().getSynonymyAttributesForTaxa();

        for (Item taxon : context.getIncludedTaxa()) {
            List<TextAttribute> taxonSynonymyAttributes = taxaSynonymyAttributes.get(taxon);

            if (SearchUtils.taxonMatches(_searchText, taxon, SearchUtils.getSynonymyStringsForTaxon(taxon, taxaSynonymyAttributes))) {
                builder.appendText(taxonFormatter.formatItemDescription(taxon));
                builder.increaseIndent();
                for (TextAttribute attr : taxonSynonymyAttributes) {
                    builder.appendText(String.format("%s %s", characterFormatter.formatCharacterDescription(attr.getCharacter()), attributeFormatter.formatAttribute(attr)));
                }
                builder.decreaseIndent();
            }
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Taxa");

        return true;
    }

}
