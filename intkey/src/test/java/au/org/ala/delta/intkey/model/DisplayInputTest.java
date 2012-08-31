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

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.DisplayInputDirective;
import au.org.ala.delta.intkey.directives.FileInputDirective;
import au.org.ala.delta.intkey.directives.PreferencesDirective;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;

public class DisplayInputTest extends IntkeyDatasetTestCase {

    @Test
    public void testDisplayInput() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        
        for (Character ch: context.getDataset().getCharactersAsList()) {
            if (!(ch instanceof MultiStateCharacter)) {
                System.out.println(ch.getCharacterId());
            }
        }

        File tempLogFile = File.createTempFile("DisplayInputTest", null);
        tempLogFile.deleteOnExit();

        context.setLogFile(tempLogFile);

        File tempJournalFile = File.createTempFile("DisplayInputTest", null);
        tempJournalFile.deleteOnExit();

        context.setJournalFile(tempJournalFile);

        URL directiveFileUrl = getClass().getResource("/input/test_directives_file.txt");
        File directivesFile = new File(directiveFileUrl.toURI());

        // first, process the sample file as a preferences file, and ensure that
        // the directive call is not output
        // to the journal or log

        new PreferencesDirective().parseAndProcess(context, directivesFile.getAbsolutePath());

        String logFileAsString = FileUtils.readFileToString(tempLogFile);
        assertFalse(logFileAsString.contains("*DEFINE CHARACTERS preferencestest 1"));

        String journalFileAsString = FileUtils.readFileToString(tempJournalFile);
        assertFalse(journalFileAsString.contains("*DEFINE CHARACTERS preferencestest 1"));

        // now, process the sample file as an input file. As DISPLAY INPUT has
        // not been set to ON,
        // the directive call should still not turn up in the log or journal
        // files

        new FileInputDirective().parseAndProcess(context, directivesFile.getAbsolutePath());

        logFileAsString = FileUtils.readFileToString(tempLogFile);
        assertFalse(logFileAsString.contains("*DEFINE CHARACTERS preferencestest 1"));

        journalFileAsString = FileUtils.readFileToString(tempJournalFile);
        assertFalse(journalFileAsString.contains("*DEFINE CHARACTERS preferencestest 1"));
        
        // now set DISPLAY INPUT to ON, and process the directives file as an input file again. 
        
        new DisplayInputDirective().parseAndProcess(context, "ON");        

        new FileInputDirective().parseAndProcess(context, directivesFile.getAbsolutePath());

        logFileAsString = FileUtils.readFileToString(tempLogFile);
        assertTrue(logFileAsString.contains("*DEFINE CHARACTERS preferencestest 1"));

        journalFileAsString = FileUtils.readFileToString(tempJournalFile);
        assertTrue(journalFileAsString.contains("*DEFINE CHARACTERS preferencestest 1"));
    }
}
