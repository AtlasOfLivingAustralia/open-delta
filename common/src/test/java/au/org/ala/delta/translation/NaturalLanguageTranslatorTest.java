package au.org.ala.delta.translation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ConforDirectiveFileParser;

/**
 * Tests the NaturalLanguageTranslator class.

 */
public class NaturalLanguageTranslatorTest extends TestCase {

	
	private NaturalLanguageTranslator _naturalLanguageTranslator;
	private DataSetTranslator _dataSetTranslator;
	private DeltaContext _context;
	private TypeSetter _typeSetter;
	private ByteArrayOutputStream _bytes;
	private static final String DEFAULT_DATASET_PATH="/dataset/simple/tonat";
	private static final String SAMPLE_DATASET_PATH="/dataset/sample/tonat_simple";
	private static final String PONERINI_DATASET_PATH="/dataset/ponerini/tonats";
	private static final String VIDE_DATASET_PATH="/dataset/vide/tonats";
	
	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		_typeSetter = new TypeSetter(pout, 78);
		
		_context = new DeltaContext();
		_naturalLanguageTranslator = new NaturalLanguageTranslator(_context, _typeSetter);
		_dataSetTranslator = new DataSetTranslator(_context, _naturalLanguageTranslator);
	}
	
	public void testBasicTranslation() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_dataSetTranslator.translate();
		checkResult("default.txt");
	}
	
	@Test
	/**
	 * Tests the OMIT REDUNDANT VARIANT ATTRIBUTES directive works correctly.
	 */
	public void testOmitRedundantVariantAttributes() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_context.setOmitRedundantVariantAttributes(true);
		
		_dataSetTranslator.translate();
		checkResult("redundant_variant_attr_omitted.txt");
	}
	
	@Test
	/**
	 * Tests the INSERT REDUNDANT VARIANT ATTRIBUTES directive works correctly.
	 */
	public void testInsertRedundantVariantAttributes() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_context.setOmitRedundantVariantAttributes(false);
		
		_dataSetTranslator.translate();
		checkResult("redundant_variant_attr_included.txt");
	}
	
	@Test
	/**
	 * Tests the natural language output handles the LINK CHARACTERS directive correctly.
	 */
	public void testLinkedCharacters() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		Set<Integer> linkedCharacters = new HashSet<Integer>();
		linkedCharacters.add(4);
		linkedCharacters.add(5);
		
		_context.linkCharacters(linkedCharacters);
		
		_dataSetTranslator.translate();
		checkResult("linked_characters.txt");
	}
	
	public void testSimpleSampleTranslation() throws Exception{
		initialiseContext(SAMPLE_DATASET_PATH);
		
		_dataSetTranslator.translate();
		checkResult("/dataset/sample/expected_results/default.txt");
	}
	
	public void testSimplePoneriniTranslation() throws Exception {
		initialiseContext(PONERINI_DATASET_PATH);	
		
		_dataSetTranslator.translate();
		checkResult("/dataset/ponerini/expected_results/default.txt");
	}
	
	/**
	 * This test method actually takes quite a while so might not be suitable for the normal
	 * build as it slows down the test cycle a fair bit.
	 */
	public void zztestSimpleVideTranslation() throws Exception {
		initialiseContext(VIDE_DATASET_PATH);	
		
		_dataSetTranslator.translate();
		checkResult("/dataset/vide/expected_results/default.txt");
	}
	
	/**
	 * Reads in specs/chars/items from the simple test data set but no other configuration.
	 * Test cases can manually configure the DeltaContext before doing the translation.
	 * @throws Exception if there was an error reading the input files.
	 */
	private void initialiseContext(String path) throws Exception {
		
		File specs = classloaderPathToFile(path);

		ConforDirectiveFileParser parser = ConforDirectiveFileParser.createInstance();
		parser.parse(specs, _context);
	}
	
	
	/**
	 * Checks the result of the translation is identical to the contents of the supplied file.
	 *
	 * @param expectedResultsFileName the name of the file - the path "/dataset/simple/expected_results/" is 
	 * prepended before loading the file contents.
	 * @throws Exception if there is an error reading the file.
	 */
	private void checkResult(String expectedResultsFileName) throws Exception {
		
		if (expectedResultsFileName.indexOf('/') < 0) {
			expectedResultsFileName =  "/dataset/simple/expected_results/" + expectedResultsFileName;
		}
		String expectedResults = classLoaderPathToString(expectedResultsFileName);
		
		if (System.getProperty("line.separator") != "\n") {
			expectedResults = expectedResults.replaceAll("\n", System.getProperty("line.separator"));
		}
		expectedResults = expectedResults.trim();
		String actualResults = actualResults().trim();
		
		// This is here because I keep getting bitten by end of line issues and the test failure
		// comparison editor doesn't display them.
		for (int i=0; i<expectedResults.length(); i++) {
			if (expectedResults.charAt(i) != actualResults.charAt(i)) {
				System.out.println("First wrong character @ position "+i);
				System.out.println("Expected: "+Integer.toHexString((int)expectedResults.charAt(i))+", found: "+Integer.toHexString((int)actualResults.charAt(i)));
				break;
			}
		}
		
		assertEquals(expectedResults, actualResults);
	}
	
	private File classloaderPathToFile(String path) throws URISyntaxException {
		URL resource = getClass().getResource(path);
		return new File(resource.toURI());
	}
	
	private String classLoaderPathToString(String path) throws Exception {
		File file = classloaderPathToFile(path);
		
		return FileUtils.readFileToString(file, "Cp1252");
	}
	
	private String actualResults() {
		return new String(_bytes.toByteArray(), Charset.forName("UTF-8"));
	}
	
}
