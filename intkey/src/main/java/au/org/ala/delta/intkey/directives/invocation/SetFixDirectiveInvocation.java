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

public class SetFixDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {

        if (_value == true && context.charactersFixed()) {
            context.getUI().displayErrorMessage("Characters already fixed.");
            return false;
        }

        if (_value == true && context.getUsedCharacters().isEmpty()) {
            context.getUI().displayErrorMessage("No characters in specimen description to fix.");
            return false;
        }

        context.setCharactersFixed(_value);
        return true;
    }

}
