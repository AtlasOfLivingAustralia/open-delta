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
package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.validation.DirectiveError.Error;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.key.directives.KeyDirectiveParser;
import au.org.ala.delta.util.Utils;

/**
 * Tests for the OUTPUT DIRECTORY directive
 * @author ChrisF
 *
 */
public class OutputDirectoryTest extends TestCase {

    // **** Tests for OUPUT DIRECTORY directive **** 
    
    //Test basic case - directory should be created
    @Test
    public void testCreateOutputDirectory() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testOutputDirectoryInputFile1");
        File directivesFile = new File(directivesFileURL.toURI());

        KeyContext context = new KeyContext(directivesFile);

        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        parser.parse(directivesFile, context);

        assertTrue(context.getOutputFileManager().getTypesettingFileOutputDirectory().exists());
        assertEquals(Utils.createFileFromPath("foo", directivesFile.getParentFile()), context.getOutputFileManager().getTypesettingFileOutputDirectory());
    }

    //Test existing file name supplied as directory name - this should result in an error
    @Test
    public void testSupplyExistingFileNameForDirectory() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testOutputDirectoryInputFile2");
        File directivesFile = new File(directivesFileURL.toURI());

        KeyContext context = new KeyContext(directivesFile);

        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        SimpleObserver observer = new SimpleObserver();
        parser.registerObserver(observer);
        parser.parse(directivesFile, context);

        List<Exception> parseExceptions = observer.getExceptions();
        assertEquals(1, parseExceptions.size());
        
        assertEquals(Error.DIRECTORY_DOES_NOT_EXIST_CANNOT_CREATE.getErrorNumber(), ((DirectiveException)parseExceptions.get(0)).getError().getErrorNumber());
    }

    private class SimpleObserver implements DirectiveParserObserver {

        List<Exception> exceptions = new ArrayList<Exception>();

        @Override
        public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) throws DirectiveException {
            // do nothing

        }

        @Override
        public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
            // do nothing

        }

        @Override
        public void finishedProcessing() {
            // do nothing
        }

        @Override
        public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> directive, Exception ex) throws DirectiveException {
            exceptions.add(ex);
        }

        public List<Exception> getExceptions() {
            return exceptions;
        }
    }
    
    

}
