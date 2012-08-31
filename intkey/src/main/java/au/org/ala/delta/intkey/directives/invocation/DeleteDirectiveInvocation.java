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

public class DeleteDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private List<Character> _characters;
    private boolean suppressUnusedCharacterWarning;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    public void setSuppressUnusedCharacterWarning(boolean suppressUnusedCharacterWarning) {
        this.suppressUnusedCharacterWarning = suppressUnusedCharacterWarning;
    }

    @Override
    public boolean execute(IntkeyContext context) {

        if (context.getUsedCharacters().isEmpty() && !suppressUnusedCharacterWarning) {
            // TODO this warning really should be displayed BEFORE the user is
            // prompted to select characters
            // to delete.
            context.getUI().displayErrorMessage("No character values have been used to describe the specimen");
            return false;
        }

        for (Character ch : _characters) {
            context.removeValueForCharacter(ch);
        }

        context.specimenUpdateComplete();

        return true;
    }

}
