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
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.rtf.RTFBuilder;

public class CharactersDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private List<Character> _characters;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        CharacterFormatter characterFormatter = new CharacterFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true, false);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Character ch : _characters) {
            builder.appendText(characterFormatter.formatCharacterDescription(ch));
            builder.increaseIndent();

            if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter msChar = (MultiStateCharacter) ch;
                for (int i = 0; i < msChar.getNumberOfStates(); i++) {
                    builder.appendText(characterFormatter.formatState(msChar, i + 1));
                }
            } else if (ch instanceof NumericCharacter<?>) {
                NumericCharacter<?> numChar = (NumericCharacter<?>) ch;
                builder.appendText(characterFormatter.formatUnits(numChar));
            }

            builder.decreaseIndent();
            builder.appendText("");
        }
        
        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Characters");

        return true;
    }

}
