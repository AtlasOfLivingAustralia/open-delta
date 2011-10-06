package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.TranslatorTest;
import au.org.ala.delta.translation.NaturalLanguageTypeSetter;

public abstract class NaturalLangaugeTranslatorTest extends TranslatorTest {

	protected AbstractDataSetTranslator _dataSetTranslator;
	protected NaturalLanguageTypeSetter _typeSetter;
	protected Printer _printer;

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
}