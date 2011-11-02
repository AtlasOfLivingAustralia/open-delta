package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import junit.framework.TestCase;

public class CharactersFileLoadTest extends TestCase {

    @Test
    public void testLoadCharacters() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/kchars");
        File directivesFile = new File(directivesFileURL.toURI());
        
        Key key = new Key();
        key.calculateKey(directivesFile);
    }
}
