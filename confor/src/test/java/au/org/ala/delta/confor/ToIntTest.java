package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import junit.framework.TestCase;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.IntkeyDatasetFileReader;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

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
		
		File expectedIChars = urlToFile("/dataset/sample/expected_results/ichars");
		File expectedIItems = urlToFile("/dataset/sample/expected_results/iitems");
		
		IntkeyDataset expectedDataSet = IntkeyDatasetFileReader.readDataSet(expectedIChars, expectedIItems);
	
		assertEquals(expectedDataSet.getNumberOfCharacters(), dataSet.getNumberOfCharacters());
		assertEquals(expectedDataSet.getNumberOfTaxa(), dataSet.getNumberOfTaxa());
	
		/*for (int i=1; i<=expectedDataSet.getNumberOfTaxa(); i++) {
			Item item = dataSet.getTaxon(i);
			Item expectedItem = expectedDataSet.getTaxon(i);
			
			assertEquals(expectedItem.getDescription(), item.getDescription());
			assertEquals(expectedItem.getImageCount(), item.getImageCount());
			assertEquals(expectedItem.getLinkFiles(), item.getLinkFiles());
			
			for (int j=1; j<=expectedDataSet.getNumberOfCharacters(); j++) {
				Character character = dataSet.getCharacter(j);
				Character expectedCharacter = expectedDataSet.getCharacter(j);
				
				assertEquals(expectedCharacter.getDescription(), character.getDescription());
				
				Attribute attr = item.getAttribute(character);
				Attribute expectedAttribute = expectedItem.getAttribute(expectedCharacter);
				
				assertEquals(expectedAttribute.getValueAsString(), attr.getValueAsString());
			}
			
		}*/
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToIntTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
