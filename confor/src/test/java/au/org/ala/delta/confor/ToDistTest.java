package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.ItemsFileHeader;

/**
 * Tests the CONFOR todist process.
 */
public class ToDistTest extends TestCase {
	
	@Test
	public void testSampleToDist() throws Exception {
		
		File tointDirectory = urlToFile("/dataset/");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(tointDirectory, dest);
		
		String tokeyFilePath = FilenameUtils.concat(dest.getAbsolutePath(), "sample/todis");
		
		CONFOR.main(new String[]{tokeyFilePath});
		
		String expectedDItemsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/expected_results/ditems");
		
		BinaryKeyFile expectedDItems = new BinaryKeyFile(expectedDItemsFilename, BinFileMode.FM_READONLY);
		
//		String actualDItemsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/ditems");
//		
//		BinaryKeyFile ditems = new BinaryKeyFile(actualDItemsFilename, BinFileMode.FM_READONLY);
//		
		
		List<Integer> header = expectedDItems.readIntegerList(1, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
		ItemsFileHeader itemsHeader = new ItemsFileHeader();
		itemsHeader.fromInts(header);
		int numRecords = expectedDItems.getLength() / BinaryKeyFile.RECORD_LENGTH_BYTES;
		
		for (int i=1; i<numRecords; i++) {
				
			System.out.println("Record: "+i);
			
		if (i>=itemsHeader.getHeadingRecord() && i<itemsHeader.getCharacterMaskRecord()){
				// The header has the date in it so will be different.
		 	}
			else {
				List<Integer> expectedRecord = expectedDItems.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
		//		List<Integer> actualRecord = ditems.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				System.out.println(expectedRecord);
			//	System.out.println(actualRecord);
			
				System.out.println("Expected as string: ");
				char[] chars = expectedDItems.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES).toCharArray();
				for (int c=0; c<chars.length; c++) {
					System.out.print(chars[c]);
				}
				
				//assertEquals(expectedRecord, actualRecord);
			}
			System.out.println();
			
//			char[] actualChars = ditems.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES).toCharArray();
//			for (int c=0; c<actualChars.length; c++) {
//				System.out.print(actualChars[c]);
//			}
//			System.out.println();
//			
			System.out.println();
		}
		
		
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToDistTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
