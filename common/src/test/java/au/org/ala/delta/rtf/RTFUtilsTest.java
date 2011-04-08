package au.org.ala.delta.rtf;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests the RTFUtils class.
 */
public class RTFUtilsTest extends TestCase {

	/**
	 * Tests the stripFormatting method leaves plain text untouched.
	 */
	@Test
	public void testStripFormattingStringPlainText() {
	
		String text = "I am simple text";
		assertEquals(text, RTFUtils.stripFormatting(text));
		
	}

}
