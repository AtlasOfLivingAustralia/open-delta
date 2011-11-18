package au.org.ala.delta.delfor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the DirectivesFileFormatter class.
 */
public class DirectivesFileFormatterTest extends TestCase {

	private DirectivesFileFormatter _formatter;
	private DelforContext _context;
	
	@Before
	public void setUp() {
		_context = new DelforContext();
		_formatter = new DirectivesFileFormatter(_context);
	}
	
	
	@Test
	public void testReformatSpecs() throws Exception {
		File specs = urlToFile("/dataset/sample/specs");
		
		_formatter.reformat(specs);
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = DirectivesFileFormatterTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
	
}
