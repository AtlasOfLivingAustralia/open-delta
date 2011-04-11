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
	private DeltaContext _context;
	private TypeSetter _typeSetter;
	private ByteArrayOutputStream _bytes;
	
	
	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes);
		_typeSetter = new TypeSetter(pout, 78);
		_context = new DeltaContext();
		initialiseContext();
		_naturalLanguageTranslator = new NaturalLanguageTranslator(_context, _typeSetter);
	}
	
	public void testBasicTranslation() throws Exception {
		
		_naturalLanguageTranslator.translate();
		checkResult("default.txt");
	}
	
	@Test
	/**
	 * Tests the OMIT REDUNDANT VARIANT ATTRIBUTES directive works correctly.
	 */
	public void testOmitRedundantVariantAttributes() throws Exception {
		
		_context.setOmitRedundantVariantAttributes(true);
		
		_naturalLanguageTranslator.translate();
		checkResult("redundant_variant_attr_omitted.txt");
	}
	
	@Test
	/**
	 * Tests the INSERT REDUNDANT VARIANT ATTRIBUTES directive works correctly.
	 */
	public void testInsertRedundantVariantAttributes() throws Exception {
		_context.setOmitRedundantVariantAttributes(false);
		
		_naturalLanguageTranslator.translate();
		checkResult("redundant_variant_attr_included.txt");
	}
	
	@Test
	/**
	 * Tests the natural language output handles the LINK CHARACTERS directive correctly.
	 */
	public void testLinkedCharacters() throws Exception {
		Set<Integer> linkedCharacters = new HashSet<Integer>();
		linkedCharacters.add(4);
		linkedCharacters.add(5);
		
		_context.linkCharacters(linkedCharacters);
		
		_naturalLanguageTranslator.translate();
		checkResult("linked_characters.txt");
	}
	
	/**
	 * Reads in specs/chars/items from the simple test data set but no other configuration.
	 * Test cases can manually configure the DeltaContext before doing the translation.
	 * @throws Exception if there was an error reading the input files.
	 */
	private void initialiseContext() throws Exception {
		
		File specs = classloaderPathToFile("/dataset/simple/tonat");

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
		String expectedResults = classLoaderPathToString("/dataset/simple/expected_results/"+expectedResultsFileName);
		
		String actualResults = actualResults();
		
		assertEquals(expectedResults, actualResults);
	}
	
	private File classloaderPathToFile(String path) throws URISyntaxException {
		URL resource = getClass().getResource(path);
		return new File(resource.toURI());
	}
	
	private String classLoaderPathToString(String path) throws Exception {
		File file = classloaderPathToFile(path);
		
		return FileUtils.readFileToString(file);
	}
	
	private String actualResults() {
		return new String(_bytes.toByteArray(), Charset.forName("Cp1252"));
	}
	
}
