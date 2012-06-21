package au.org.ala.delta.model.attribute;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Extends the DefaultAttributeChunkFormatter to format numbers to 5 significant figures.
 */
public class SignificantFiguresAttributeChunkFormatter extends DefaultAttributeChunkFormatter {

    private static final int NUM_SIGNIFICANT_FIGURES = 5;

    public SignificantFiguresAttributeChunkFormatter(boolean encloseInCommentBrackets, String rangeSeparator) {
        super(encloseInCommentBrackets, rangeSeparator);
    }

    /**
     * Overrides formatNumber in the parent class to format the number to 5 significant figures.  Trailing
     * zeros are stripped.
     * Note: for compatibility with the original CONFOR significant figures are only applied to values after
     * the decimal place. (e.g. 123456.7 will be formatted as 123456, not 123460)
     * @param number the number to format.
     * @return the supplied number as a String.
     */
    @Override
    public String formatNumber(BigDecimal number) {

        int significantFigures = determinePrecision(number);
        MathContext context = new MathContext(significantFigures, RoundingMode.HALF_UP);

        BigDecimal result = number.round(context);
        result = result.stripTrailingZeros();
        return result.toPlainString();
    }

    /**
     * Determines the precision to format the number to.
     * @param number the number to determine.
     * @return the precision (number of figures) to format the supplied number to.
     */
    private int determinePrecision(BigDecimal number) {

        // Adjust the precision because CONFOR doesn't strictly do significant figures - it only applies them
        // to values after the decimal place.
        // e.g. 123456.78 to 5 significant figures should be 123460 but CONFOR will output 123456

        int digitsToLeftOfPoint = number.precision() - number.scale();
        return Math.max(NUM_SIGNIFICANT_FIGURES, digitsToLeftOfPoint);

    }
}
