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

public class CharacterExcluderTest extends TestCase {

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
	public void testExcludeCharacters() {
		
		Integer[] toExclude = {1, 2, 3, 4};
		

		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			_dataSet.getCharacter(i).setDescription("character "+i);
		}
		
		CharacterExcluder excluder = new CharacterExcluder(Arrays.asList(toExclude));
		excluder.format(_context, _dataSet);
		
		assertEquals(85, _dataSet.getNumberOfCharacters());
		
		for (int i=1; i<=85; i++) {
			assertEquals("character "+(i+4), _dataSet.getCharacter(i).getDescription());
		}
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = CharacterExcluderTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
