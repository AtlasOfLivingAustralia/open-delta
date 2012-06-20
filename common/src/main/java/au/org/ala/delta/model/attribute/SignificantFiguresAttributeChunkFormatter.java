package au.org.ala.delta.model.attribute;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Extends the DefaultAttributeChunkFormatter to format numbers to 5 significant figures.
 */
public class SignificantFiguresAttributeChunkFormatter extends DefaultAttributeChunkFormatter {

    private static final int NUM_SIGNIFICANT_FIGURES = 5;

    private MathContext context = new MathContext(NUM_SIGNIFICANT_FIGURES, RoundingMode.HALF_UP);
    public SignificantFiguresAttributeChunkFormatter(boolean encloseInCommentBrackets, String rangeSeparator) {
        super(encloseInCommentBrackets, rangeSeparator);
    }

    /**
     * Overrides formatNumber in the parent class to format the number to 5 significant figures.  Trailing
     * zeros are stripped.
     * @param number the number to format.
     * @return the supplied number as a String.
     */
    @Override
    public String formatNumber(BigDecimal number) {
        return number.round(context).stripTrailingZeros().toPlainString();
    }
}
