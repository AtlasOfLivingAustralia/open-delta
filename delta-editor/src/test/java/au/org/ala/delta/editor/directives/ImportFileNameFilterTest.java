package au.org.ala.delta.editor.directives;

import java.io.File;

import junit.framework.TestCase;

/**
 * Tests the ImportFileNameFilter class.
 */
public class ImportFileNameFilterTest extends TestCase {

	public void testFilterWithSinglePattern() {

		String exclusionPattern = "*.bak";

		check(exclusionPattern, "test.bak", false);
		check(exclusionPattern, "bak", true);
		check(exclusionPattern, ".bak", false);
		check(exclusionPattern, "test", true);
		check(exclusionPattern, "test.bak1", true);
	}

	public void testFilterWithMulitplePatterns() {

		String exclusionPattern = "*.bak;*.tmp;fred";

		check(exclusionPattern, "test.bak", false);
		check(exclusionPattern, "rem.tmp", false);
		check(exclusionPattern, "fred", false);
		check(exclusionPattern, "fred.fred", true);
		check(exclusionPattern, "fred.bak", false);
	}

	private void check(String exclusionPatterns, String input, boolean expectedResult) {
		ImportFileNameFilter filter = new ImportFileNameFilter(exclusionPatterns);

		assertEquals(expectedResult, filter.accept(new File(input)));
	}
}
