package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import junit.framework.TestCase;

public class InputFileTest extends TestCase {

    @Test
    public void testInputFile() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/scriptToCallInputFile");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey(directivesFile);
        
        assertEquals(3d, key.getContext().getABase());
        assertEquals(3d, key.getContext().getRBase());
        assertEquals(3d, key.getContext().getVaryWt());
    }
    
    @Test
    public void testNonExistentInputFile() throws Exception {
        
    }
}
