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

import au.org.ala.delta.intkey.directives.DefineEndIdentifyDirective;
import au.org.ala.delta.intkey.directives.DisplayEndIdentifyDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;

public class EndIdentifyTest extends IntkeyDatasetTestCase {

    public void testEndIdentify() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        new DisplayEndIdentifyDirective().parseAndProcess(context, "ON");
        new DefineEndIdentifyDirective().parseAndProcess(context, "\"define characters endidentify 1\"");
        new UseDirective().parseAndProcess(context, "6,2");

        Character ch = context.getDataset().getCharacter(1);

        assertEquals(1, context.getCharactersForKeyword("endidentify").size());
        assertEquals(ch, context.getCharactersForKeyword("endidentify").get(0));
    }

    public void testEndIdentifyCommandsIgnoredIfDisplayEndIdentifyOff() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        new DisplayEndIdentifyDirective().parseAndProcess(context, "OFF");
        new DefineEndIdentifyDirective().parseAndProcess(context, "\"define characters endidentify 1\"");
        new UseDirective().parseAndProcess(context, "6,2");

        // An attempt to get characters for keyword "endidentify" should result
        // in an IllegalArgumentException
        // due to the keyword having not been defined.
        boolean exceptionThrown = false;

        try {
            context.getCharactersForKeyword("endidentify");
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
}
