package au.org.ala.delta;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import au.org.ala.delta.directives.DirectiveFileParser;

/**
 * Tests DELTA file parsing.
 */
public class DirectiveFileParserTest extends TestCase {

	/**
	 * Tests the sample data set can be parsed using the "toint" file as input.
	 */
	public void testParseWithSampleDataSetToInt() throws Exception {
		URL tointURL = getClass().getResource("/dataset/sample/toint");
		
		File toint = new File(tointURL.toURI());
		
		DeltaContext context = new DeltaContext();
		
		DirectiveFileParser p = new DirectiveFileParser();
		p.parse(toint, context);
		
	}
	
//	/**
//	 * Tests the sample data set can be parsed using the "tonat" file as input.
//	 */
//	public void testParseWithSampleDataSetToNat() throws Exception {
//		URL tointURL = getClass().getResource("/dataset/sample/tonat");
//		
//		File toint = new File(tointURL.toURI());
//		
//		DeltaContext context = new DeltaContext();
//		
//		DirectiveFileParser p = new DirectiveFileParser();
//		p.parse(toint, context);
//		
//		NaturalLanguageTranslator nt = new NaturalLanguageTranslator(context, new TypeSetter());
//		
//		nt.translate();
//	}
	
}
