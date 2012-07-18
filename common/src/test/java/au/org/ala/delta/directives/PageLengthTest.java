package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.OutputFileManager;
import au.org.ala.delta.io.OutputFileSelector;
import junit.framework.TestCase;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Tests the PageLength directive class
 */
public class PageLengthTest extends TestCase {

    /**
     * Tests the PageLength directive behaves correctly when given correct input.
     */
    @Test
    public void testPageLengthCorrectInput() throws Exception {

        DeltaContext context = runTest("80");
        int actualPageLength = getPageLength(context.getOutputFileSelector());
        assertEquals(80, actualPageLength);
    }

    /**
     * Tests the PageLength directive behaves correctly when given correct input with leading whitespace.
     */
    @Test
    public void testPageLengthLeadingWhiteSpace() throws Exception {

        DeltaContext context = runTest("  80");
        int actualPageLength = getPageLength(context.getOutputFileSelector());
        assertEquals(80, actualPageLength);
    }

    @Test
    public void testNegativeInput() throws Exception {
        try {
            runTest("-1");
            fail("Should have failed validation.");
        }
        catch (DirectiveException e) {
            assertEquals(DirectiveError.Error.VALUE_LESS_THAN_MIN.getErrorNumber(), e.getError().getErrorNumber());
        }
    }

    private DeltaContext runTest(String input) throws Exception {
        DeltaContext context = new DeltaContext();
        context.newParsingContext();
        PageLength pageLength = new PageLength();
        pageLength.parseAndProcess(context, input);

        return context;
    }

    private int getPageLength(OutputFileSelector outputFileSelector) throws Exception {
        Field pageLength = OutputFileManager.class.getDeclaredField("_outputPageLength");
        pageLength.setAccessible(true);
        return (Integer)pageLength.get(outputFileSelector);
    }
}
