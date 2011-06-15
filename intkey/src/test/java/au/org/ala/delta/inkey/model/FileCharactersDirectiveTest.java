package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.FileCharactersDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;

import junit.framework.TestCase;

/**
 * Unit tests for the FILE CHARACTERS directive.
 * @author ChrisF
 *
 */
public class FileCharactersDirectiveTest extends TestCase {
    
    @Test 
    public void testSetValidCharactersFile() throws Exception {
        IntkeyContext context = new IntkeyContext(null);
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");
        
        File fileCharacters = new File(icharsFileUrl.toURI());
        
        new FileCharactersDirective().parseAndProcess(context, fileCharacters.getAbsolutePath());
        
        assertEquals(fileCharacters, context.getCharactersFile());
    }
    
    
   @Test
    public void testSetInvalidCharactersFile() throws Exception {
    }
    
}
