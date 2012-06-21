package au.org.ala.delta.model.attribute;

import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests the SignificantFiguresAttributeChunkFormatter class.
 */
public class SignificantFiguresAttributeChunkFormatterTest extends TestCase {

    /**
     * Tests the formatting of a number.
     */
    @Test
    public void testNumberFormatting() {
        SignificantFiguresAttributeChunkFormatter formatter = new SignificantFiguresAttributeChunkFormatter(false, "-");
        String[] input = new String[] {"0.000001", "10", "123456.0", "1234.5", "1234.55", "4444.4444", "1.00", "12345.0"};
        String[] expected = new String[] {"0.000001", "10", "123456", "1234.5", "1234.6", "4444.4", "1", "12345"};
        for (int i=0; i<input.length; i++) {
            String result = formatter.formatNumber(new BigDecimal(input[i]));
            assertEquals(input[i], expected[i], result);
        }
    }

}
