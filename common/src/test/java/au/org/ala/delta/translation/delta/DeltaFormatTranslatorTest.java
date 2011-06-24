package au.org.ala.delta.translation.delta;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.TranslatorTest;

/**
 * Tests the DeltaFormatTranslator.
 */
public class DeltaFormatTranslatorTest extends TranslatorTest {

	protected static final String DEFAULT_DATASET_PATH = "/dataset/sample/fillin";
	
	protected DeltaFormatTranslator _dataSetTranslator;
	protected Printer _printer;
	
	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		_printer = new Printer(pout, 78);
		_context = new DeltaContext();
		_printer = new Printer(_context.getPrintStream(), _context.getPrintWidth());
		_dataSetTranslator = new DeltaFormatTranslator(_context, _printer);
	}
	
	public void testItemsTranslation() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_dataSetTranslator.translate();
		
	}
}
