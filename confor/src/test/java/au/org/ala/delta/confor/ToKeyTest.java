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

/**
 * Tests the CONFOR tokey process.
 */
public class ToKeyTest extends TestCase {
	
	@Test
	public void testSampleToKey() throws Exception {
		
		File tointDirectory = urlToFile("/dataset/");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(tointDirectory, dest);
		
		String tokeyFilePath = FilenameUtils.concat(dest.getAbsolutePath(), "sample/tokey");
		
		CONFOR.main(new String[]{tokeyFilePath});
		
		String expectedKCharsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/expected_results/kchars");
		
		BinaryKeyFile expectedKChars = new BinaryKeyFile(expectedKCharsFilename, BinFileMode.FM_READONLY);
		
		String actualKCharsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/kchars");
		BinaryKeyFile kchars = new BinaryKeyFile(actualKCharsFilename, BinFileMode.FM_READONLY);
		
		int numRecords = expectedKChars.getLength() / BinaryKeyFile.RECORD_LENGTH_BYTES;
		int actualNumRecords = kchars.getLength() / BinaryKeyFile.RECORD_LENGTH_BYTES;
		
		int count = Math.min(numRecords, actualNumRecords);
		
		for (int i=1; i<=count; i++) {
			List<Integer> expectedRecord = expectedKChars.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
			List<Integer> actualRecord = kchars.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
			
			System.out.println("Record: "+i);
			System.out.println(expectedRecord);
			System.out.println(actualRecord);
			
			System.out.println("Expected as string: ");
			System.out.println(expectedKChars.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES));
			
			System.out.println();
		}
		
		
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToKeyTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
