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
	
	
	@Test
	public void testRtfToHtml() {
		String text = "\\i{}Ornithospermum\\i0{} Dumoulin, \\i{}Tema\\i0{} Adans.";
		String result = RTFUtils.rtfToHtml(text);
		
		String expected = "<I>Ornithospermum</I> Dumoulin, <I>Tema</I> Adans.";
		assertEquals(expected, result);
	}

}
