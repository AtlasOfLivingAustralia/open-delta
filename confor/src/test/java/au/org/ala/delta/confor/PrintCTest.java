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
		String expected = FileUtils.readFileToString(expectedFile);

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(_samplePath, "printc.prt.new"));
		String actual = FileUtils.readFileToString(actualFile);

		System.out.print(actual);
	}

	@Override
	protected String directivesFileName() {
		return "printc";
	}
	
	
}
