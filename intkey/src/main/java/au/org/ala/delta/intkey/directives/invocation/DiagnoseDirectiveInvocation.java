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

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class DiagnoseDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;
    private List<Character> _characters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // the SPECIMEN cannot be selected with the taxa for this directive.
        // Simply ignore
        // it if "SPECIMEN" is supplied
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        IntkeyDataset dataset = context.getDataset();

        ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        AttributeFormatter attributeFormatter = new AttributeFormatter(false, false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, context.getDataset().getOrWord());

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        // saved information which will be altered by DIAGNOSE.

        // derive diagnostic character set for specified items from set of
        // masked-in characters.
        for (Item taxon : _taxa) {
            // output taxon name
            builder.appendText(itemFormatter.formatItemDescription(taxon));

            Specimen s = new Specimen(context.getDataset(), false, true, true, MatchType.OVERLAP);

            // process preset characters first
            for (Character ch : _characters) {
                // use value for taxon's attribute for that character

                
            }

            // calculate further separation characters for current taxon
            // get diagnose order
            //

        }

        builder.endDocument();
        context.getUI().displayRTFReport(builder.toString(), "Diagnose");

        return true;
    }

}
