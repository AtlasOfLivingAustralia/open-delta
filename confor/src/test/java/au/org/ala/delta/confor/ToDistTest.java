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

		CONFOR.main(new String[] { tokeyFilePath });

		String expectedDItemsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/expected_results/ditems");
		BinaryKeyFile expectedDItems = new BinaryKeyFile(expectedDItemsFilename, BinFileMode.FM_READONLY);
		String actualDItemsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/ditems");
		BinaryKeyFile ditems = new BinaryKeyFile(actualDItemsFilename, BinFileMode.FM_READONLY);

		List<Integer> header = expectedDItems.readIntegerList(1, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
		ItemsFileHeader itemsHeader = new ItemsFileHeader();
		itemsHeader.fromInts(header);
		int numRecords = expectedDItems.getLength() / BinaryKeyFile.RECORD_LENGTH_BYTES;

		int headerRecord = 0;
		for (int i = 1; i < numRecords; i++) {

			System.out.println("Record: " + i);

			List<Integer> expectedRecord = expectedDItems.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
			List<Integer> actualRecord = ditems.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
			System.out.println(expectedRecord);
			System.out.println(actualRecord);
			// header
			if (i == 1) {
				for (int j = 0; j < expectedRecord.size(); j++) {
					// The length of the heading is different as the date format
					// is different.
					if (j != 7 && j != 8) {
						assertEquals(expectedRecord.get(j), actualRecord.get(j));
					} else if (j == 8) {
						headerRecord = expectedRecord.get(j);
					}
				}
			} else if (i != headerRecord) {

				for (int j = 0; j < expectedRecord.size(); j++) {
					int expectedBits = expectedRecord.get(j);
					String bitsString = toBitString(expectedBits);
					System.out.print(bitsString + " ");
				}
				System.out.println();

				for (int j = 0; j < actualRecord.size(); j++) {
					int actualBits = actualRecord.get(j);
					String bitsString = toBitString(actualBits);
					System.out.print(bitsString + " ");
				}
				System.out.println();
				System.out.println("Expected as string: ");
				char[] chars = expectedDItems.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES).toCharArray();
				for (int c = 0; c < chars.length; c++) {
					System.out.print(chars[c]);
				}
				System.out.println();
				char[] actualchars = ditems.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES).toCharArray();
				for (int c = 0; c < actualchars.length; c++) {
					System.out.print(actualchars[c]);
				}
				System.out.println();
				List<Float> expectedFloats = expectedDItems.readFloatList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				System.out.println(expectedFloats);
				List<Float> actualFloats = ditems.readFloatList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				System.out.println(actualFloats);

			    System.out.println();

				assertEquals(expectedRecord, actualRecord);
			}
		}
	}

	private String toBitString(int expectedBits) {
		String bitsString = Integer.toBinaryString(expectedBits);
		int length = bitsString.length();
		if (length < 32) {
			for (int k = 0; k < 32 - length; k++) {
				bitsString = "0" + bitsString;
			}
		}
		return bitsString;
	}

	private File urlToFile(String urlString) throws Exception {
		URL url = ToDistTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
