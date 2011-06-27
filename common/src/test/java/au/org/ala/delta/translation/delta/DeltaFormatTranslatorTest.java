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
		_printer = new Printer(pout, 80);
		_context = new DeltaContext();
		
		_dataSetTranslator = new DeltaFormatTranslator(_context, _printer);
	}
	
	public void testItemsTranslation() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_dataSetTranslator.translate();
		
		checkResult("/dataset/sample/expected_results/deltaformatitems.txt");
		
	}

	/**
	 * Checks the result of the translation is identical to the contents of the supplied file.
	 *
	 * @param expectedResultsFileName the name of the file - the path "/dataset/simple/expected_results/" is 
	 * prepended before loading the file contents.
	 * @param vide true if we are checking the Vide dataset - we do a few things a bit differently
	 * to CONFOR that I am taking into account.
	 * @throws Exception if there is an error reading the file.
	 */
	protected void checkResult(String expectedResultsFileName) throws Exception {
		
		String expectedResults = classLoaderPathToString(expectedResultsFileName);
		
		boolean dosEol = expectedResults.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		String lineSeparator = System.getProperty("line.separator");
		if (!lineSeparator.equals(expectedLineSeparator)) {
			expectedResults = expectedResults.replaceAll(expectedLineSeparator, lineSeparator);
		}
		
		expectedResults = expectedResults.trim();
		String actualResults = actualResults().trim();
		
		// Replace windows code page chars
		
		actualResults = actualResults.replaceAll("\\\\emdash\\{\\}", "\u2014");
		actualResults = actualResults.replaceAll("\\\\endash\\{\\}", "\u2013");
		
		
		// This is here because I keep getting bitten by end of line issues and the test failure
		// comparison editor doesn't display them.
		for (int i=0; i<expectedResults.length() && i<actualResults.length(); i++) {
			if (expectedResults.charAt(i) != actualResults.charAt(i)) {
				System.out.println("First wrong character @ position "+i);
				System.out.println("Expected: "+Integer.toHexString((int)expectedResults.charAt(i))+", found: "+Integer.toHexString((int)actualResults.charAt(i)));
				break;
			}
		}
		
		assertEquals(expectedResults, actualResults);
	}
}
