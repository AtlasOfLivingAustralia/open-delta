package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
		
		File tointDirectory = urlToFile("/dataset/");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(tointDirectory, dest);
		
		String tointFilePath = FilenameUtils.concat(dest.getAbsolutePath(), "sample/toint");
		
		CONFOR.main(new String[]{tointFilePath});
		
		File ichars = new File(FilenameUtils.concat(dest.getAbsolutePath(), "sample/ichars"));
		File iitems = new File(FilenameUtils.concat(dest.getAbsolutePath(), "sample/iitems"));
		
		IntkeyDataset dataSet = IntkeyDatasetFileReader.readDataSet(ichars, iitems);
		
		File expectedIChars = urlToFile("/dataset/sample/expected_results/ichars");
		File expectedIItems = urlToFile("/dataset/sample/expected_results/iitems");
		
		IntkeyDataset expectedDataSet = IntkeyDatasetFileReader.readDataSet(expectedIChars, expectedIItems);
		
		assertEquals(expectedDataSet.getNumberOfCharacters(), dataSet.getNumberOfCharacters());
		assertEquals(expectedDataSet.getNumberOfTaxa(), dataSet.getNumberOfTaxa());
	
		assertEquals(expectedDataSet.getCharacterKeywordImages(), dataSet.getCharacterKeywordImages());
		assertEquals(expectedDataSet.getHeading(), dataSet.getHeading());
		// These seem to be unused now.
		//assertEquals(expectedDataSet.getHelpCharNotesFormattingInfo(), dataSet.getHelpCharNotesFormattingInfo());
		//assertEquals(expectedDataSet.getMainCharNotesFormattingInfo(), dataSet.getMainCharNotesFormattingInfo());
		assertEquals(expectedDataSet.getOrWord(), dataSet.getOrWord());
		assertEquals(expectedDataSet.getSubHeading(), dataSet.getSubHeading());
		assertEquals(expectedDataSet.getValidationString(), dataSet.getValidationString());
		assertEquals(expectedDataSet.getOverlayFonts(), dataSet.getOverlayFonts());
		assertEquals(expectedDataSet.getStartupImages(), dataSet.getStartupImages());
//		assertEquals(expectedDataSet.getSynonymyAttributesForTaxa(), dataSet.getSynonymyAttributesForTaxa());
		assertEquals(expectedDataSet.getSynonymyCharacters(), dataSet.getSynonymyCharacters());
		assertEquals(expectedDataSet.getTaxonKeywordImages(), dataSet.getTaxonKeywordImages());
		
		
		
		
		
		for (int i=1; i<=expectedDataSet.getNumberOfTaxa(); i++) {
			Item item = dataSet.getTaxon(i);
			Item expectedItem = expectedDataSet.getTaxon(i);
			
			assertEquals(expectedItem.getDescription(), item.getDescription());
			assertEquals(expectedItem.getImageCount(), item.getImageCount());
			assertEquals(expectedItem.getLinkFiles(), item.getLinkFiles());
			
			for (int j=1; j<=expectedDataSet.getNumberOfCharacters(); j++) {
				Character character = dataSet.getCharacter(j);
				Character expectedCharacter = expectedDataSet.getCharacter(j);
				
				assertEquals(expectedCharacter.getDescription(), character.getDescription());
				
				Attribute expectedAttribute = expectedDataSet.getAttribute(i, j);
				Attribute attr = dataSet.getAttribute(i, j);
				
				
				assertEquals(expectedAttribute.getValueAsString(), attr.getValueAsString());
				
			}
			
		}
		
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToIntTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
