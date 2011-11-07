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

        Key key = new Key();
        key.calculateKey(directivesFile);
    }
}
