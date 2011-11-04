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
	
	@Test
	public void testTripInnerCommentsMultipleInnerComments() {
		_formatter = new Formatter(CommentStrippingMode.STRIP_INNER, AngleBracketHandlingMode.RETAIN, false, false);
		String text = "<1 long, split, serrate, 1 simple <potentially split>, 2 split <potentially serrate>>";
		String result = _formatter.defaultFormat(text);
		assertEquals("<1 long, split, serrate, 1 simple, 2 split>", result);
		
	}
}
