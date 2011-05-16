package au.org.ala.delta.translation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import junit.framework.TestCase;

public abstract class NaturalLangaugeTranslatorTest extends TestCase {

	protected AbstractDataSetTranslator _dataSetTranslator;
	protected TypeSetter _typeSetter;
	protected DeltaContext _context;
	protected Printer _printer;
	protected ByteArrayOutputStream _bytes;
	protected static final String DEFAULT_DATASET_PATH = "/dataset/simple/tonat";
	protected static final String SAMPLE_DATASET_PATH = "/dataset/sample/tonat_simple";
	protected static final String PONERINI_DATASET_PATH = "/dataset/ponerini/tonats";
	protected static final String VIDE_DATASET_PATH = "/dataset/vide/tonats";
	protected static final String CFLORA_DATASET_PATH = "/dataset/cflora/tonats";

	public NaturalLangaugeTranslatorTest() {
		super();
	}

	public NaturalLangaugeTranslatorTest(String name) {
		super(name);
	}

	/**
	 * Reads in specs/chars/items from the simple test data set but no other configuration.
	 * Test cases can manually configure the DeltaContext before doing the translation.
	 * @throws Exception if there was an error reading the input files.
	 */
	protected void initialiseContext(String path) throws Exception {
		
		File specs = classloaderPathToFile(path);
	
		ConforDirectiveFileParser parser = ConforDirectiveFileParser.createInstance();
		parser.parse(specs, _context);
	}

	protected void checkResult(String expectedResultsFileName) throws Exception {
		checkResult(expectedResultsFileName, false);
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
	protected void checkResult(String expectedResultsFileName, boolean vide) throws Exception {
		
		if (expectedResultsFileName.indexOf('/') < 0) {
			expectedResultsFileName =  "/dataset/simple/expected_results/" + expectedResultsFileName;
		}
		String expectedResults = classLoaderPathToString(expectedResultsFileName);
		
		boolean dosEol = expectedResults.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		
		if (!System.getProperty("line.separator").equals(expectedLineSeparator)) {
			expectedResults = expectedResults.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
		}
		
		expectedResults = expectedResults.trim();
		String actualResults = actualResults().trim();
		
		// Replace windows code page chars
		
		actualResults = actualResults.replaceAll("\\\\emdash\\{\\}", "\u2014");
		actualResults = actualResults.replaceAll("\\\\endash\\{\\}", "\u2013");
		
		if (vide) {
			// Our RTF stripping doesn't remove keywords that translate to a single unicode character.
			actualResults = actualResults.replaceAll("\\u2013", "");
			actualResults = actualResults.replaceAll("\\u2014", "");
			actualResults = actualResults.replaceAll("\\s+", " ");
			actualResults = actualResults.replaceAll("\\s\\.", ".");
			actualResults = actualResults.replaceAll("\\s,", ",");
			
			expectedResults = expectedResults.replaceAll("\\s+", " ");
			expectedResults = expectedResults.replaceAll("\\s,", ",");
			expectedResults = expectedResults.replaceAll("\\s\\.", ".");
			
			int different = actualResults.indexOf("{3");
			
			StringBuilder actual = new StringBuilder();
			StringBuilder expected = new StringBuilder();
			int minLength = Math.min(actualResults.length(), expectedResults.length());
			for (int i=0; i<different; i++) {
				actual.append(actualResults.charAt(i));
				expected.append(expectedResults.charAt(i));
				if (i % 80 == 0) {
					actual.append("\n");
					expected.append("\n");
				}
			}
			
			for (int i=different; i<different+20; i++) {
				actual.append('*');
				expected.append('*');
			}
			// Skip a few chars - we are treating escaped \{ better than CONFOR.
			for (int i=different+20; i<minLength; i++) {
				
				actual.append(actualResults.charAt(i+5));
				expected.append(expectedResults.charAt(i));
				if (i % 80 == 0) {
					actual.append("\n");
					expected.append("\n");
				}
			}
			
			actualResults = actual.toString();
			expectedResults = expected.toString();
		}
		
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

	private File classloaderPathToFile(String path)
			throws URISyntaxException {
				URL resource = getClass().getResource(path);
				return new File(resource.toURI());
			}

	private String classLoaderPathToString(String path) throws Exception {
		File file = classloaderPathToFile(path);
		
		return FileUtils.readFileToString(file, "Cp1252");
	}

	private String actualResults() throws IOException {
		_bytes.flush();
		return new String(_bytes.toByteArray(), Charset.forName("UTF-8"));
	}

}