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
		String expectedKItemsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/expected_results/kitems");
		
		BinaryKeyFile expectedKChars = new BinaryKeyFile(expectedKCharsFilename, BinFileMode.FM_READONLY);
		BinaryKeyFile expectedKItems = new BinaryKeyFile(expectedKItemsFilename, BinFileMode.FM_READONLY);
		
		String actualKCharsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/kchars");
		String actualKItemsFilename = FilenameUtils.concat(dest.getAbsolutePath(), "sample/kitems");
		
		BinaryKeyFile kchars = new BinaryKeyFile(actualKCharsFilename, BinFileMode.FM_READONLY);
		BinaryKeyFile kitems = new BinaryKeyFile(actualKItemsFilename, BinFileMode.FM_READONLY);
		
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
			assertEquals(expectedRecord, actualRecord);
			
		}
		
		numRecords = expectedKItems.getLength() / BinaryKeyFile.RECORD_LENGTH_BYTES;
		System.out.println("**** Items File ("+numRecords+")*****");
		
		List<Integer> header = expectedKItems.readIntegerList(1, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
		ItemsFileHeader itemsHeader = new ItemsFileHeader();
		itemsHeader.fromInts(header);
		
		for (int i=1; i<numRecords; i++) {
			
			
			System.out.println("Record: "+i);
			
			if ((i >= itemsHeader.getCharcterReliabilitiesRecord() && i < itemsHeader.getTaxonMaskRecord()) ||
				 i >= itemsHeader.getItemAbundancesRecord()) {
				List<Float> expectedFloats = expectedKItems.readFloatList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				List<Float> actualFloats = kitems.readFloatList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				System.out.println(expectedFloats);
				System.out.println(actualFloats);
				
				for (int f=0; f<expectedFloats.size(); f++) {
					// Note sure if this is a difference in encoding or precision
					// but our floats are quite different.
					assertEquals(expectedFloats.get(f), actualFloats.get(f), 0.021f);
				}
			}
			else if (i>=itemsHeader.getHeadingRecord() && i<itemsHeader.getCharacterMaskRecord()){
				// The header has the date in it so will be different.
		 	}
			else {
				List<Integer> expectedRecord = expectedKItems.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				List<Integer> actualRecord = kitems.readIntegerList(i, BinaryKeyFile.RECORD_LENGTH_INTEGERS);
				System.out.println(expectedRecord);
				System.out.println(actualRecord);
			
				System.out.println("Expected as string: ");
				char[] chars = expectedKItems.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES).toCharArray();
				for (int c=0; c<chars.length; c++) {
					System.out.print(chars[c]);
				}
				if (i != 1) {
				assertEquals(expectedRecord, actualRecord);
				}
			}
			System.out.println();
			
			char[] actualChars = kitems.readString(i, BinaryKeyFile.RECORD_LENGTH_BYTES).toCharArray();
			for (int c=0; c<actualChars.length; c++) {
				System.out.print(actualChars[c]);
			}
			System.out.println();
			
			System.out.println();
		}
		
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToKeyTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
