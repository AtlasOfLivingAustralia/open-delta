package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;

/**
 * Base class for CONFOR integration tests.
 */
public abstract class ConforTestCase extends TestCase {

	protected String _directivesFilePath;
	protected String _samplePath;
	
	@Before
	public void setUp() throws Exception {
		File todisDirectory = urlToFile("/dataset/");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(todisDirectory, dest);
	
		_samplePath = FilenameUtils.concat(dest.getAbsolutePath(), "sample");
		_directivesFilePath = FilenameUtils.concat(_samplePath, directivesFileName());
	
		
	}
	
	protected void runConfor() throws Exception {
		CONFOR.main(new String[] { _directivesFilePath });
	}
	
	protected abstract String directivesFileName();
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToDistTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
