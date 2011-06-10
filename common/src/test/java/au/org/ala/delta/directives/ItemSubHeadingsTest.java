package au.org.ala.delta.directives;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import junit.framework.TestCase;

/**
 * Tests the ItemSubHeadings class.
 */
public class ItemSubHeadingsTest extends TestCase {

	/**
	 * Tests processing of the directive with correct data.
	 */
	@Test
	public void testItemSubHeadingsProcessing() throws Exception {
		String data = "#87. Transverse section of lamina.\n" + "#96. Leaf epidermis.\n"
				+ "#124. Pollen ultrastructure.";

		ItemSubHeadings directive = new ItemSubHeadings();

		DeltaContext context = new DeltaContext();

		directive.parseAndProcess(context, data);

		assertEquals("Transverse section of lamina.", context.getItemSubheading(87));
		assertEquals("Leaf epidermis.", context.getItemSubheading(96));
		assertEquals("Pollen ultrastructure.", context.getItemSubheading(124));
		for (int i = 1; i <= 124; i++) {
			boolean expectedResult = (i == 87 || i == 96 || i == 124);
			assertEquals(Integer.toString(i), expectedResult, context.getItemSubheading(i) != null);
		}

	}

	/**
	 * Tests processing of the directive with correct data and a delimiter. Not
	 * actually sure how the delimiter is supposed to be used right now....
	 */
	@Test
	public void testItemSubHeadingsProcessingWithDelimiter() throws Exception {
		String data = "!\n" + "#87. !Transverse section of lamina.!\n" + "#96. !Leaf epidermis.!\n"
				+ "#124. !Pollen ultrastructure.!";

		ItemSubHeadings directive = new ItemSubHeadings();

		DeltaContext context = new DeltaContext();

		directive.parseAndProcess(context, data);

		assertEquals("Transverse section of lamina.", context.getItemSubheading(87));
		assertEquals("Leaf epidermis.", context.getItemSubheading(96));
		assertEquals("Pollen ultrastructure.", context.getItemSubheading(124));
		for (int i = 1; i <= 124; i++) {
			boolean expectedResult = (i == 87 || i == 96 || i == 124);
			assertEquals(Integer.toString(i), expectedResult, context.getItemSubheading(i) != null);
		}

	}

	/**
	 * Tests processing of the directive with correct data and an invalid
	 * delimiter.
	 */
	@Test
	public void testItemSubHeadingsProcessingWithInvalidDelimiter() throws Exception {

		String[] invalidDelimiters = new String[] { "*", "#", "<", ">" };

		for (String delimeter : invalidDelimiters) {
			String data = " " + delimeter + "\n #1. ";

			ItemSubHeadings directive = new ItemSubHeadings();

			DeltaContext context = new DeltaContext();

			try {
				directive.parseAndProcess(context, data);
				fail("Invalid delimeter should have caused an exception");
			} catch (Exception e) {
			}

		}

	}
}
