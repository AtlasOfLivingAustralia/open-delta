/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.confor;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.ItemsFileHeader;

/**
 * Tests the CONFOR todist process.
 */
public class ToDistTest extends ConforTestCase {
	
	@Test
	public void testSampleToDist() throws Exception {

		runConfor();
		
		String expectedDItemsFilename = FilenameUtils.concat(_samplePath, "expected_results/ditems");
		BinaryKeyFile expectedDItems = new BinaryKeyFile(expectedDItemsFilename, BinFileMode.FM_READONLY);
		String actualDItemsFilename = FilenameUtils.concat(_samplePath, "ditems");
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

	protected String directivesFileName() {
		return "todis";
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

	
}
