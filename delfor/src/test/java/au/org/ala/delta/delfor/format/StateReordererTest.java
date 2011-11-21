package au.org.ala.delta.delfor.format;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.DelforDirectiveFileParser;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.MultiStateCharacter;

public class StateReordererTest extends TestCase {

	private DelforContext _context;
	private SlotFileDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		_dataSet = (SlotFileDataSet) dataSetRepository.newDataSet();
		
		_context = new DelforContext(_dataSet);
		
		DelforDirectiveFileParser parser = DelforDirectiveFileParser.createInstance();
		File specs = urlToFile("/dataset/sample/specs");
		parser.parse(specs, _context);
		
		
		
	}
	
	@Test
	public void testStateReorder() {
		int charNum = 8;
		Integer[] newOrder = {6, 4, 3, 1, 7, 2, 5};
		MultiStateCharacter character = (MultiStateCharacter)_dataSet.getCharacter(charNum);
		
		for (int i=1; i<=character.getNumberOfStates(); i++) {
			character.setState(i, "state "+i);
		}
		
		StateReorderer stateReorderer = new StateReorderer(charNum, Arrays.asList(newOrder));
		stateReorderer.format(_context, _dataSet);
		
		
		String[] expectedOrder = {
				"state 6",
				"state 4",
				"state 3",
				"state 1",
				"state 7",
				"state 2",
				"state 5"
			};
		int i=1;
		for (String expected : expectedOrder) {
			assertEquals(expected, character.getState(i++));
		}
		
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = StateReordererTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
