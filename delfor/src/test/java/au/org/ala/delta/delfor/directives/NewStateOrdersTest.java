package au.org.ala.delta.delfor.directives;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.format.FormattingAction;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;

/**
 * Tests the NewStateOrders class.
 */
public class NewStateOrdersTest extends TestCase {

	private DelforContext _context;
	private SlotFileDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		_dataSet = (SlotFileDataSet) dataSetRepository.newDataSet();
		
		_context = new DelforContext(_dataSet);
	}
	
	@Test
	public void testNewStateOrders() throws Exception {
		NewStateOrders newStateOrders = new NewStateOrders();
		
		String test = "5,2:1 10,4:1-3:5";
		newStateOrders.parseAndProcess(_context, test);
		
		List<FormattingAction> actions = _context.getFormattingActions();
		assertEquals(2, actions.size());
		
		
	}
	
}
