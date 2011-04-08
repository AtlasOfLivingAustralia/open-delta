package au.org.ala.delta.translation;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;

import junit.framework.TestCase;

/**
 * Tests the formatter class.
 */
public class FormatterTest extends TestCase {

	
	private Formatter _formatter = new Formatter(new DeltaContext());
	
	@Test
	public void testStripCommentsInputWithNoComments() {
		String textWithNoComments = "This has no comments";
		
		assertEquals(textWithNoComments, _formatter.stripComments(textWithNoComments));
	}
	
	@Test
	public void testStripCommentsSingleComment() {
		String textWithComment = "This has <one> comment in the middle";
		
		assertEquals("This has comment in the middle", _formatter.stripComments(textWithComment));
	
		textWithComment = "<This> has one comment at the start";
		assertEquals("has one comment at the start", _formatter.stripComments(textWithComment));
		
		textWithComment = "This has one comment at the <end>";
		assertEquals("This has one comment at the", _formatter.stripComments(textWithComment));
	}
	
	@Test
	public void testStripCommentsMultipleComments() {
		String textWithComment = "This has <more> than <one> comment.";
		
		assertEquals("This has than comment.", _formatter.stripComments(textWithComment));
	}
	
	
}
