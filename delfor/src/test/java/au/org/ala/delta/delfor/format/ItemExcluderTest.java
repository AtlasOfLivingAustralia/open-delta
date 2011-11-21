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
import au.org.ala.delta.model.Item;

public class ItemExcluderTest extends TestCase {

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
	public void testExcludeItems() {
		
		for (int i=1; i<=10; i++) {
			Item item = _dataSet.addItem();
			item.setDescription("item "+i);
		}
		
		Integer[] toExclude = {1, 6, 9};
		
		ItemExcluder excluder = new ItemExcluder(Arrays.asList(toExclude));
		excluder.format(_context, _dataSet);
		
		
		assertEquals(7, _dataSet.getMaximumNumberOfItems());
		
		int[] expectedNums = {2, 3, 4, 5, 7, 8, 10};
		int i=1;
		for (int num : expectedNums) {
			assertEquals("item "+num, _dataSet.getItem(i++).getDescription());
		}
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ItemExcluderTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}
