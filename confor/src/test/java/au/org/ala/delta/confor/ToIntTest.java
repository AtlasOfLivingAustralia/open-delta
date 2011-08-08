package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

/**
 * Tests the CONFOR toint process.
 */
public class ToIntTest extends TestCase {
	
	
	public void testSampleToInt() throws Exception {
		
		URL tointUrl = ToIntTest.class.getResource("/dataset/sample/toint");
		File tointFile = new File(tointUrl.toURI());
		CONFOR.main(new String[]{tointFile.getAbsolutePath()});
	}
	
	
}
