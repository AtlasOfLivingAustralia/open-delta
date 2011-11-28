package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Test;

public class KeyCalcuationTest extends TestCase {
    
    @Test
    public void testLoad() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/mykey");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey(directivesFile);
    }
    
    @Test
    public void testLoad2() throws Exception {
        URL directivesFileURL = getClass().getResource("/controlling_characters_simple/key");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey(directivesFile);
    }
    
//    @Test
//    public void testLoadPonerini() throws Exception {
//        File directivesFile = new File("C:\\Users\\ChrisF\\Virtualbox Shared Folder\\Cyperaceae_test\\key");
//
//        Key key = new Key();
//        key.calculateKey(directivesFile);
//    }
}
