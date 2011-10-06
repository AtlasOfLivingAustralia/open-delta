package au.org.ala.delta.confor;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Tests the PRINT CHARACTER LIST directive in the context of the sample
 * printc directives file.
 */
public class PrintCTest extends ConforTestCase {

	@Test
	public void testSamplePrintC() throws Exception {
		runConfor();
		
		File expectedFile = new File(FilenameUtils.concat(_samplePath, "expected_results/printc.prt"));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(_samplePath, "printc.prt"));
		String actual = FileUtils.readFileToString(actualFile, "utf-8");

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
		String heading = "Grass Genera"; // <Date>, eg. 11:32 05-OCT-11
		int headingIndex = expected.indexOf(heading) + 28;
		expected = expected.substring(headingIndex).trim();
		actual = actual.substring(headingIndex+2).trim();
		
		
		for (int i=0; i<expected.length(); i++) {
			if (expected.charAt(i) != actual.charAt(i)) {
				System.out.println("Difference @ char: "+i+" Expected: "+expected.charAt(i)+(int)expected.charAt(i)+", Actual: "+actual.charAt(i)+(int)actual.charAt(i));
				break;
			}
		}
		assertEquals(expected, actual);
	}

	@Override
	protected String directivesFileName() {
		return "printc";
	}
	
	
}
