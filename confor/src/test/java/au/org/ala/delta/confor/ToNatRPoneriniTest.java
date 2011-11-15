package au.org.ala.delta.confor;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Tests the PRINT CHARACTER LIST directive in the context of the sample
 * printc directives file.
 */
public class ToNatRPoneriniTest extends ConforTestCase {

	@Test
	public void testSampleToNatR() throws Exception {
		runConfor();
		
		File expectedFile = new File(FilenameUtils.concat(_samplePath, "expected_results/descrip.rtf"));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(_samplePath, "rtf/descrip.rtf"));
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
		
		actual = actual.replaceAll("\\{\\} ", "\\{\\}");
		expected = expected.replaceAll("\\{\\} ", "\\{\\}");
		actual = actual.replaceAll(" \\\\", "\\\\");
		expected = expected.replaceAll(" \\\\", "\\\\");
		
		
		// The heading contains the date so will be different.
		
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
		return "tonatr";
	}
	
	protected String getDataSet() {
		return "ponerini";
	}
	
	
}
