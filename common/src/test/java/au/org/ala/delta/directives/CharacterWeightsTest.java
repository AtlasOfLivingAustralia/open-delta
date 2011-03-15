package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import junit.framework.TestCase;

/**
 * Tests the CharacterReliabilities and CharacterWeights classes (as they are very similar)
 */
public class CharacterWeightsTest extends TestCase {

	/**
	 * Tests processing (and use of defaults) with correct data.
	 */
	public void testCharacterReliabilitiesProcessing() throws Exception {
		String data = "1,0 2-5,7 6,5 7-10,7 11-13,8 14-24,7 25,0 26,7 27,8 28-38,7 39,5 40-43,7 44,8\n"+
			"45-47,7 48,8 49-63,7 64,6 65,7 66,8 67,7 68,3 69,0 70,5 71-76,0 77,7.1 78-87,0" ;
		
		CharacterReliabilities directive = new CharacterReliabilities();
		
		DeltaContext context = new DeltaContext();
		context.setNumberOfCharacters(88);
		
		directive.process(context, data);
		
		assertEquals(0.0, context.getCharacterWeight(1));
		
		// Make sure ranges are ok.
		assertEquals(7.0, context.getCharacterWeight(2));
		assertEquals(7.0, context.getCharacterWeight(3));
		assertEquals(7.0, context.getCharacterWeight(4));
		assertEquals(7.0, context.getCharacterWeight(5));
		
		assertEquals(7.1, context.getCharacterWeight(77));
		
		// This directive initialises weights to 5...
		assertEquals(5.0, context.getCharacterWeight(88));
	}
	
	/**
	 * Tests processing of invalid (out of range) data.
	 */
	public void testCharacterReliabilitiesProcessingWithOutOfRangeValues() throws Exception {
		String data = "1,-0.01";
		
		CharacterReliabilities directive = new CharacterReliabilities();
		
		DeltaContext context = new DeltaContext();
	
		context.setNumberOfCharacters(88);
		
		try {
			directive.process(context, data);
			fail("should have thrown an exception");
		}
		catch (IllegalArgumentException e) {}
		
		data = "1,10.01";
		
		try {
			directive.process(context, data);
			fail("should have thrown an exception");
		}
		catch (IllegalArgumentException e) {}
	}
	
	/**
	 * Tests processing (and use of defaults) with correct data.
	 */
	public void testCharacterWeightsProcessing() throws Exception {
		String data = "1,3 2-10,15.7 12,32.0 13,0.03125";
		
		CharacterWeights directive = new CharacterWeights();
		
		DeltaContext context = new DeltaContext();
		context.setNumberOfCharacters(13);
		
		directive.process(context, data);
		
		assertEquals(3.0, context.getCharacterWeight(1));
		
		// Make sure ranges are ok.
		assertEquals(15.7, context.getCharacterWeight(2));
		assertEquals(15.7, context.getCharacterWeight(3));
		assertEquals(15.7, context.getCharacterWeight(10));
		
		// make sure defaults are ok
		assertEquals(1.0, context.getCharacterWeight(11));
		
		assertEquals(32.0, context.getCharacterWeight(12));
		assertEquals(0.03125, context.getCharacterWeight(13));	
	}
	
	/**
	 * Tests processing of invalid (out of range) data.
	 */
	public void testCharacterWeightsProcessingWithOutOfRangeValues() throws Exception {
		String data = "1,0.03124";
		
		CharacterWeights directive = new CharacterWeights();
		
		DeltaContext context = new DeltaContext();
		context.setNumberOfCharacters(13);
		
		try {
			directive.process(context, data);
			fail("should have thrown an exception");
		}
		catch (IllegalArgumentException e) {}
		
		data = "1,32.001";
		
		try {
			directive.process(context, data);
			fail("should have thrown an exception");
		}
		catch (IllegalArgumentException e) {}
	}
	
}
