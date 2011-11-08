package au.org.ala.delta.confor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Tests how CONFOR handles errors in the supplied directives files.
 */
public class ErrorsTest extends TestCase {

	@Test
	public void testtointwithe() throws Exception {
		File directivesFileDirectory = urlToFile("/dataset/errors");
		
		String path = directivesFileDirectory.getAbsolutePath();
		
		//runAndTest(path, "tointwithe", "tointe.err");
	}
	
	protected void runAndTest(String path, String directivesFile, String resultFileName) throws Exception, IOException {
		
		String directivesFileFullPath = FilenameUtils.concat(path, directivesFile);
		CONFOR.main(new String[] { directivesFileFullPath });

		File expectedFile = new File(FilenameUtils.concat(FilenameUtils.concat(path, "expected_results"), resultFileName));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(path, resultFileName));
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
		
		for (int i=0; i<expected.length(); i++) {
			if (expected.charAt(i) != actual.charAt(i)) {
				System.out.println("Difference @ char: "+i+" Expected: "+expected.charAt(i)+(int)expected.charAt(i)+", Actual: "+actual.charAt(i)+(int)actual.charAt(i));
				break;
			}
		}
		assertEquals(expected.trim(), actual.trim());
	}

	private File urlToFile(String urlString) throws Exception {
		URL url = ErrorsTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
	
	
}
