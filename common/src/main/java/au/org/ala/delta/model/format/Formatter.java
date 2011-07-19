package au.org.ala.delta.model.format;

import java.io.StringReader;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * Base class for DELTA formatters.
 */
public class Formatter {

    protected static Pattern EMPTY_COMMENT_PATTERN = Pattern.compile("<\\s*>");

    protected boolean _stripComments;
    protected boolean _stripNestedCommentsOnly;
    protected boolean _stripFormatting;
    protected boolean _replaceAngleBrackets;

    public Formatter(boolean stripComments, boolean stripNestedCommentsOnly, boolean replaceAngleBrackets, boolean stripFormatting) {
        _stripComments = stripComments;
        _stripNestedCommentsOnly = stripNestedCommentsOnly;
        _stripFormatting = stripFormatting;
        _replaceAngleBrackets = replaceAngleBrackets;
    }

    /**
     * Formats the supplied text according to how this Formatter was configured
     * on construction.
     * 
     * @param text
     *            the text to format.
     * @return the formatted text.
     */
    public String defaultFormat(String text) {

        return defaultFormat(text, _stripComments, _stripFormatting);
    }

    public String defaultFormat(String text, boolean stripComments, boolean stripFormatting) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }

        if (stripFormatting) {
            text = RTFUtils.stripFormatting(text);
        }
        if (stripComments) {
            text = stripComments(text);
        }
        if (!stripComments && _replaceAngleBrackets) {
            text = replaceAngleBrackets(text);
        }
        // Stripping formatting can leave extra whitespace lying around
        // sometimes.
        text = text.replaceAll(" +", " ");
        return text;
    }

    /**
     * @param text
     * @return true if the supplied text is surrounded by a pair of angle
     *         brackets, ignoring any nested pairs of angle brackets.
     */
    private boolean textSurroundedByAngleBrackets(String text) {
        if (text.charAt(0) == '<' && text.charAt(text.length() - 1) == '>') {
            int openBrackets = 0;
            boolean entireStringEnclosed = true;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                if (c == '<') {
                    openBrackets++;
                }

                if (c == '>') {
                    openBrackets--;
                }

                if (openBrackets == 0 && i < text.length() - 1) {
                    entireStringEnclosed = false;
                    break;
                }
            }

            return entireStringEnclosed;
        }

        return false;
    }

    /**
     * Replace angle brackets with parentheses in the supplied text. If the text
     * is completely surrounded by a pair of angle brackets, these brackets will
     * be stripped off. The text contained by the angle brackets will then be
     * returned, with any nested angle brackets substituted for parentheses.
     * 
     * @param text
     *            the text to replace angle brackets in
     * @return the text with angle brackets replaced.
     */
    public String replaceAngleBrackets(String text) {
        if (textSurroundedByAngleBrackets(text)) {
            text = text.substring(1, text.length() - 1);
        }

        text = text.replace('<', '(');
        text = text.replace('>', ')');
        return text;
    }

    /**
     * Removes the comments which are areas of text enclosed in angle brackets.
     * 
     * If the formatter has been created with the "stripNestedCommentsOnly"
     * parameter set to true, only nested comments will be stripped in the case
     * that the full text is a single comment. In this case, the comment text
     * will be return with any nested comments removed, and the surrounding
     * angle brackets removed.
     * 
     * @param text
     *            the text to remove comments from.
     * @return the text without comments.
     */
    public String stripComments(String text) {
        if (_stripNestedCommentsOnly) {
            if (textSurroundedByAngleBrackets(text)) {
                text = text.substring(1, text.length() - 1);
            }
        }

        CommentStripper stripper = new CommentStripper(text);
        try {
            stripper.parse();
        } catch (Exception e) {
            return text;
        }

        return stripper.getValue();
    }

    /**
     * Removes DELTA style comments from a string.
     */
    class CommentStripper extends CommentExtractor {

        private StringBuilder _value = new StringBuilder();

        public CommentStripper(String value) {
            super(value);
        }

        @Override
        public void comment(String comment) {
        }

        @Override
        public void value(String value) {
            if (value == null) {
                return;
            }
            value = value.trim();
            if (StringUtils.isNotBlank(value)) {
                _value.append(value.trim());
                _value.append(" ");
            }

        }

        public String getValue() {
            return _value.toString().trim();
        }
    }

    /**
     * Parses a string looking for comments.
     */
    public static abstract class CommentExtractor extends AbstractStreamParser {

        protected char _previousChar = 0;

        public CommentExtractor(String value) {
            super(null, new StringReader(value));
        }

        @Override
        public void parse() throws ParseException {
            readNext();

            while (_currentInt >= 0) {

                if (matchesComment()) {
                    comment(readComment());
                } else {
                    value(readValue());
                }
            }
        }

        public String readValue() throws ParseException {
            StringBuffer value = new StringBuffer();
            while (_currentInt >= 0 && !matchesComment()) {
                value.append(_currentChar);
                readNext();
            }
            return value.toString();
        }

        protected boolean matchesComment() {
            if (_currentChar == '<') {
                return _position == 1 || Character.isWhitespace(_previousChar);
            }
            return false;
        }

        @Override
        protected int readNext() throws ParseException {
            _previousChar = _currentChar;
            return super.readNext();
        }

        public abstract void comment(String comment) throws ParseException;

        public abstract void value(String value) throws ParseException;
    }

}
