package au.org.ala.delta.delfor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.AbstractObservableDataSet;

/**
 * Tests the DirectivesFileFormatter class.
 */
public class DirectivesFileFormatterTest extends TestCase {

	private DirectivesFileFormatter _formatter;
	private DelforContext _context;
	
	@Before
	public void setUp() {
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		AbstractObservableDataSet dataSet = (AbstractObservableDataSet) dataSetRepository.newDataSet();

		_context = new DelforContext(dataSet);
		_formatter = new DirectivesFileFormatter(_context);
	}
	
	
	@Test
	public void testReformatSpecs() throws Exception {
		File specs = urlToFile("/dataset/sample/specs");
		_context.addReformatFile(specs);
		_formatter.reformat();
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = DirectivesFileFormatterTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
	
}
