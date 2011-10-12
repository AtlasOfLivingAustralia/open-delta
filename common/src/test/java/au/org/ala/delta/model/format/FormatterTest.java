package au.org.ala.delta.model.format;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

/**
 * Tests the Formatter class.
 */
public class FormatterTest extends TestCase {

	private Formatter _formatter;

	
	@Test
	public void testStripInnerComments() {
		
		_formatter = new Formatter(CommentStrippingMode.STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, false);
		String text = "not pseudopetiolate <Test <implicit>>";
		String result = _formatter.defaultFormat(text);
		assertEquals("not pseudopetiolate Test", result);
	}
	
	@Test
	public void testStripInnerCommentsOuterCommentEmpty() {
		
		_formatter = new Formatter(CommentStrippingMode.STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, false);
		String text = "not pseudopetiolate <<implicit>>";
		String result = _formatter.defaultFormat(text);
		assertEquals("not pseudopetiolate", result);
	}
	
}
