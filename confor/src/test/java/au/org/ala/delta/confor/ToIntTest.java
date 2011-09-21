package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import junit.framework.TestCase;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.IntkeyDatasetFileReader;

/**
 * Tests the CONFOR toint process.
 */
public class ToIntTest extends TestCase {
	
	@Test
	public void testSampleToInt() throws Exception {
		
		File tointFile = urlToFile("/dataset/sample/toint");
		CONFOR.main(new String[]{tointFile.getAbsolutePath()});
		
		File ichars = urlToFile("/dataset/sample/ichars");
		File iitems = urlToFile("/dataset/sample/iitems");
		
		IntkeyDataset dataSet = IntkeyDatasetFileReader.readDataSet(ichars, iitems);
		assertEquals(89, dataSet.getNumberOfCharacters());
		assertEquals(14, dataSet.getNumberOfTaxa());
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToIntTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
