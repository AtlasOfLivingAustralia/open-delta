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

import java.io.File;
import java.net.URL;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.PreferencesDirective;
import au.org.ala.delta.model.Character;

public class PreferencesDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testPreferences() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        URL preferencesFileUrl = getClass().getResource("/input/test_directives_file.txt");
        File preferencesFile = new File(preferencesFileUrl.toURI());

        new PreferencesDirective().parseAndProcess(context, preferencesFile.getAbsolutePath());

        Character firstDatasetChar1 = context.getDataset().getCharacter(1);

        assertEquals(1, context.getCharactersForKeyword("preferencestest").size());
        assertEquals(firstDatasetChar1, context.getCharactersForKeyword("preferencestest").get(0));

        loadNewDatasetInExistingContext("/dataset/controlling_characters_simple/intkey.ink", context);

        Character secondDatasetChar1 = context.getDataset().getCharacter(1);
        assertEquals(1, context.getCharactersForKeyword("preferencestest").size());
        assertEquals(secondDatasetChar1, context.getCharactersForKeyword("preferencestest").get(0));
    }
}
