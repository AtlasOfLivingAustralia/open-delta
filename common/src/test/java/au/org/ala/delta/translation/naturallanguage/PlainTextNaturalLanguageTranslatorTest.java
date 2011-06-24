package au.org.ala.delta.translation.naturallanguage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;


import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.PlainTextTypeSetter;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageTranslator;

/**
 * Tests the production of plain text natural language.  This test is more of an integration
 * test than a unit test - it relies on the collaboration of several classes to produce the
 * natural language output.
 */
public class PlainTextNaturalLanguageTranslatorTest extends NaturalLangaugeTranslatorTest {

	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		_printer = new Printer(pout, 78);
		_typeSetter = new PlainTextTypeSetter(_printer);
		_context = new DeltaContext();
		ItemFormatter itemFormatter = new ItemFormatter(false, false, false, true, false);
		CharacterFormatter characterFormatter = new CharacterFormatter(false, true, false, true);
		AttributeFormatter attributeFormatter = new AttributeFormatter(false, true);
		_dataSetTranslator = new NaturalLanguageTranslator(_context, _typeSetter, _printer, itemFormatter, characterFormatter, attributeFormatter);
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
	
	public void testSampleTranslationWithImplictValues() throws Exception{
		initialiseContext(SAMPLE_DATASET_PATH);
		_context.setInsertImplicitValues(true);
		_dataSetTranslator.translate();
		checkResult("/dataset/sample/expected_results/withimplicitvalues.txt");
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
		checkResult("/dataset/vide/expected_results/default.txt", true);
	}
	
	/**
	 * This test method actually takes quite a while so might not be suitable for the normal
	 * build as it slows down the test cycle a fair bit.
	 */
	public void zztestSimpleCFloraTranslation() throws Exception {
		initialiseContext(CFLORA_DATASET_PATH);	
		
		_dataSetTranslator.translate();
		checkResult("/dataset/cflora/expected_results/default.txt");
	}
	
}
