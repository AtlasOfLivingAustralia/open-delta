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
package au.org.ala.delta.intkey.model;

import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DeleteDirective;
import au.org.ala.delta.intkey.directives.RestartDirective;
import au.org.ala.delta.intkey.directives.SetFixDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;

public class SetFixDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testFixCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        List<au.org.ala.delta.model.Character> usedCharacters;
        
        assertFalse(context.charactersFixed());

        Character chIncluding = context.getDataset().getCharacter(1);
        Character chLongevity = context.getDataset().getCharacter(2);
        Character chCulmsHeight = context.getDataset().getCharacter(3);

        // Fix two characters then add a value for an additional one

        new UseDirective().parseAndProcess(context, "1,foo");
        new UseDirective().parseAndProcess(context, "2,1");

        new SetFixDirective().parseAndProcess(context, "ON");
        
        assertTrue(context.charactersFixed());
        assertEquals(2, context.getFixedCharactersList().size());
        assertTrue(context.getFixedCharactersList().contains(1));
        assertTrue(context.getFixedCharactersList().contains(2));

        new UseDirective().parseAndProcess(context, "3,2");

        usedCharacters = context.getUsedCharacters();
        assertTrue(usedCharacters.contains(chIncluding));
        assertTrue(usedCharacters.contains(chLongevity));
        assertTrue(usedCharacters.contains(chCulmsHeight));

        new RestartDirective().parseAndProcess(context, null);

        // The two fixed characters should remain used after the reset.
        usedCharacters = context.getUsedCharacters();
        assertTrue(usedCharacters.contains(chIncluding));
        assertTrue(usedCharacters.contains(chLongevity));
        assertFalse(usedCharacters.contains(chCulmsHeight));

        // Delete one of the fixed characters
        new DeleteDirective().parseAndProcess(context, "1");
        usedCharacters = context.getUsedCharacters();
        assertFalse(usedCharacters.contains(chIncluding));
        assertTrue(usedCharacters.contains(chLongevity));
        assertFalse(usedCharacters.contains(chCulmsHeight));

        // Re-use the character that was just deleted
        new UseDirective().parseAndProcess(context, "1,foo");
        new RestartDirective().parseAndProcess(context, null);

        // The single fixed character should be the only one that remains after
        // the reset.
        usedCharacters = context.getUsedCharacters();
        assertFalse(usedCharacters.contains(chIncluding));
        assertTrue(usedCharacters.contains(chLongevity));
        assertFalse(usedCharacters.contains(chCulmsHeight));

        // Turn the FIX setting off
        new SetFixDirective().parseAndProcess(context, "OFF");
        
        assertFalse(context.charactersFixed());
        assertTrue(context.getFixedCharactersList().isEmpty());

        // The specimen should now be empty
        assertTrue(context.getUsedCharacters().isEmpty());

        // Add character values again
        new UseDirective().parseAndProcess(context, "1,foo");
        new UseDirective().parseAndProcess(context, "2,1");
        new UseDirective().parseAndProcess(context, "3,2");

        usedCharacters = context.getUsedCharacters();
        assertTrue(usedCharacters.contains(chIncluding));
        assertTrue(usedCharacters.contains(chLongevity));
        assertTrue(usedCharacters.contains(chCulmsHeight));

        // As characters are no longer fixed, the specimen should now be empty
        // after a reset.
        new RestartDirective().parseAndProcess(context, null);
        assertTrue(context.getUsedCharacters().isEmpty());
    }

}
