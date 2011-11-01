package au.org.ala.delta.dist;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * This is an integration test for the DIST program.
 */
public class DISTTest extends TestCase {

	private String _path;
	@Before
	public void setUp() throws Exception {
		File dataSetDirectory = urlToFile("/dataset");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(dataSetDirectory, dest);
		_path = dest.getAbsolutePath();
	}
	
	@Test
	public void testDISTWithSample() throws Exception {
		String path = FilenameUtils.concat(_path, "sample");
		
		runDIST(FilenameUtils.concat(path, "dist"));
		checkResults(path, "grass.nam");
		//checkResults(path, "grass.dis");
	}
	
	private void checkResults(String path, String resultFileName) throws Exception {
		
		
		java.io.File expectedFile = new File(FilenameUtils.concat(path, "expected_results/"+resultFileName));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");
	
		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(path, resultFileName));
		String actual = FileUtils.readFileToString(actualFile, "cp1252");
	
		System.out.print(actual);
		expected = replaceNewLines(expected);
		
		assertEquals(expected.trim(), actual.trim());
	}

	protected String replaceNewLines(String expected) {
		boolean dosEol = expected.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		
		if (!System.getProperty("line.separator").equals(expectedLineSeparator)) {
			expected = expected.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
		}
		return expected;
	}
	
	private void runDIST(String... args) throws Exception {
		DIST.main(args);
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = DISTTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
