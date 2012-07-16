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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class IncludeCharactersDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private List<Character> _characters;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> includedCharNumbers = new HashSet<Integer>();
        for (Character ch: _characters) {
            includedCharNumbers.add(ch.getCharacterId());
        }
        
        context.setIncludedCharacters(includedCharNumbers);
        return true;
    }

}
