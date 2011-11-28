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

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.SearchUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.rtf.RTFBuilder;

public class FindCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _searchText;

    public void setSearchText(String searchText) {
        this._searchText = searchText;
    }

    @Override
    public boolean execute(IntkeyContext context) {

        CharacterFormatter characterFormatter = new CharacterFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, false, false);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Character ch : context.getIncludedCharacters()) {
            if (SearchUtils.characterMatches(ch, _searchText, true)) {
                builder.appendText(characterFormatter.formatCharacterDescription(ch));
            }
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Find");

        return true;
    }

}
