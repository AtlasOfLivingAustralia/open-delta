package au.org.ala.delta.delfor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

public class DelforTest extends TestCase {
	private String _path;
	@Before
	public void setUp() throws Exception {
		File dataSetDirectory = urlToFile("/dataset");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(dataSetDirectory, dest);
		_path = dest.getAbsolutePath();
	}
	
	@Test
	public void testDELFORWithSample() throws Exception {
		String path = FilenameUtils.concat(_path, "sample");
		
		runDELFOR(FilenameUtils.concat(path, "reorder"));
		//checkResults(path, "chars.new");
	}
	
	private void checkResults(String path, String resultFileName) throws Exception {
		
		java.io.File expectedFile = new File(FilenameUtils.concat(path, "expected_results/"+resultFileName));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");
	
		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(path, resultFileName));
		String actual = FileUtils.readFileToString(actualFile, "cp1252");
	
		System.out.print(actual);
		expected = replaceNewLines(expected);
		
		// CONFOR leaves a lot of trailing spaces around
		expected = expected.replaceAll(" ([\\r\\n]+)", "$1");
		actual = actual.replaceAll(" ([\\r\\n]+)", "$1");
		
		
		assertEquals(expected, actual);
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
	
	private void runDELFOR(String... args) throws Exception {
		DELFOR.main(args);
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = DelforTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
