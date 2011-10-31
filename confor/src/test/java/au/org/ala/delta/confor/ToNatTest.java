package au.org.ala.delta.confor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Tests the TRANSLATE INTO NATURAL LANGUAGE directive in the context of the sample
 * tonat directives file.
 */
public class ToNatTest extends ConforTestCase {

	@Test
	public void testSampleToNat() throws Exception {
		
		String directivesFileName = "tonat";
		_directivesFilePath = FilenameUtils.concat(_samplePath, directivesFileName);
		String resultFileName = "tonat.prt";
		
		
		runAndTest(resultFileName);
	}

	
	@Test
	public void testSampleToNatWithOmitFinalComma() throws Exception {
		String directivesFileName = "tonat_omitfinalcomma";
		_directivesFilePath = FilenameUtils.concat(_samplePath, directivesFileName);
		String resultFileName = "tonat_omitfinalcomma.prt";
		
		
		runAndTest(resultFileName);
	}
	
	protected void runAndTest(String resultFileName) throws Exception, IOException {
		runConfor();
		
		File expectedFile = new File(FilenameUtils.concat(_samplePath, "expected_results/"+resultFileName));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(_samplePath, resultFileName));
		String actual = FileUtils.readFileToString(actualFile, "cp1252");

		System.out.print(actual);
		
		
		boolean dosEol = expected.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		
		if (!System.getProperty("line.separator").equals(expectedLineSeparator)) {
			expected = expected.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
		}
		// The heading contains the date so will be different.
		String heading = "Grass Genera 13:32 12-OCT-11"; // <Date>, eg. 11:32 05-OCT-11
		
		actual = actual.replaceAll("Grass Genera.*[0-9]{2}-[a-zA-Z]{3}-[0-9]{4}", heading);
		
		for (int i=0; i<expected.length(); i++) {
			if (expected.charAt(i) != actual.charAt(i)) {
				System.out.println("Difference @ char: "+i+" Expected: "+expected.charAt(i)+(int)expected.charAt(i)+", Actual: "+actual.charAt(i)+(int)actual.charAt(i));
				break;
			}
		}
		assertEquals(expected.trim(), actual.trim());
	}

	@Override
	protected String directivesFileName() {
		return "tonat";
	}
	
	
}
