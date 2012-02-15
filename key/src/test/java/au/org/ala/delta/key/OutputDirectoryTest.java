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

public class OutputDirectoryTest extends TestCase {

    //Test basic case - directory should be created
    @Test
    public void testCreateOutputDirectory() throws Exception {
        URL dataDirectoryURL = getClass().getResource("/sample");
        File dataDirectory = new File(dataDirectoryURL.toURI());

        URL directivesFileURL = getClass().getResource("/sample/testOutputDirectoryInputFile1");
        File directivesFile = new File(directivesFileURL.toURI());

        KeyContext context = new KeyContext(dataDirectory);

        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        parser.parse(directivesFile, context);

        assertEquals("foo", context.getTypesetFilesOutputDirectory().getName());
    }

    //Test existing file name supplied as directory name - this should result in an error
    @Test
    public void testSupplyExistingFileNameForDirectory() throws Exception {
        URL dataDirectoryURL = getClass().getResource("/sample");
        File dataDirectory = new File(dataDirectoryURL.toURI());

        URL directivesFileURL = getClass().getResource("/sample/testOutputDirectoryInputFile2");
        File directivesFile = new File(directivesFileURL.toURI());

        KeyContext context = new KeyContext(dataDirectory);

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
