package au.org.ala.delta.model.format;

import java.io.StringReader;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Utils;

/**
 * Base class for DELTA formatters.
 */
public class Formatter {

    protected static Pattern EMPTY_COMMENT_PATTERN = Pattern.compile("<\\s*>");

    protected boolean _stripFormatting;
    protected boolean _capitaliseFirstWord;

    protected CommentStrippingMode _commentStrippingMode;
    protected AngleBracketHandlingMode _angleBracketHandlingMode;

    public static enum CommentStrippingMode {
        RETAIN, STRIP_ALL, RETAIN_SURROUNDING_STRIP_INNER, STRIP_INNER
    }

    public static enum AngleBracketHandlingMode {
        RETAIN, REPLACE, REMOVE, REMOVE_SURROUNDING_REPLACE_INNER
    };

    public Formatter(CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
        _commentStrippingMode = commentStrippingMode;
        _angleBracketHandlingMode = angleBracketHandlingMode;
        _stripFormatting = stripFormatting;
        _capitaliseFirstWord = capitaliseFirstWord;
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
        return defaultFormat(text, _commentStrippingMode, _angleBracketHandlingMode, _stripFormatting, _capitaliseFirstWord);
    }

    public String defaultFormat(String text, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }

        if (stripFormatting) {
            text = RTFUtils.stripFormatting(text);
        }

        text = stripComments(text, commentStrippingMode);

        if (commentStrippingMode == CommentStrippingMode.RETAIN) {
            text = handleAngleBrackets(text, angleBracketHandlingMode);
        }

        // Stripping formatting can leave extra whitespace lying around
        // sometimes.
        text = text.replaceAll(" +", " ");

        if (capitaliseFirstWord) {
            text = Utils.capitaliseFirstWord(text);
        }

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
    private String handleAngleBrackets(String text, AngleBracketHandlingMode angleBracketHandlingMode) {
        switch (_angleBracketHandlingMode) {
        case REMOVE:
            text = text.replace("<", "");
            text = text.replace(">", "");
            break;
        case REPLACE:
            text = text.replace("<", "(");
            text = text.replace(">", ")");
            break;
        case REMOVE_SURROUNDING_REPLACE_INNER:
            if (textSurroundedByAngleBrackets(text)) {
                text = removeSurroundingBrackets(text);
            }
            text = text.replace("<", "(");
            text = text.replace(">", ")");
        case RETAIN:
            // do nothing
            break;
        default:
            throw new IllegalArgumentException("Unrecognized angle bracket handling mode");
        }

        return text;
    }

	private String removeSurroundingBrackets(String text) {
		text = text.substring(1, text.length() - 1);
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
    private String stripComments(String text, CommentStrippingMode commentStrippingMode) {
        switch (commentStrippingMode) {
        case RETAIN:
            // no modification
            return text;
        case RETAIN_SURROUNDING_STRIP_INNER:
            if (textSurroundedByAngleBrackets(text)) {
                text = removeSurroundingBrackets(text);
            }
            break;
        case STRIP_ALL:
        case STRIP_INNER:
            break;
        default:
            throw new IllegalArgumentException("Unrecognized comment stripping mode");
        }

        CommentStripper stripper = new CommentStripper(text, commentStrippingMode == CommentStrippingMode.STRIP_INNER);
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

        public CommentStripper(String value, boolean stripInnerComments) {
            super(value, stripInnerComments);
        }

        @Override
        public void comment(String comment) {
        	if (_stripInnerComments) {
        		
        		_value.append(trimComment(comment));
        	}
        }
        
        private String trimComment(String comment) {
        	// comment is surrounded by <>.
        	if (textSurroundedByAngleBrackets(comment)) {
        		comment = removeSurroundingBrackets(comment).trim();
        		if (comment.length() > 0) {
        			comment = "<"+comment.trim()+">";
        		}
        	}
        	return comment;
        }

        @Override
        public void value(String value) {
            if (value == null) {
                return;
            }
            _value.append(value);

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
        protected boolean _stripInnerComments;

        public CommentExtractor(String value, boolean stripInnerComments) {
            super(null, new StringReader(value));
            _stripInnerComments = stripInnerComments;
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
                return _position == 1 || Character.isWhitespace(_previousChar) || _previousChar == '<' || _previousChar == '>';
            }
            return false;
        }

        @Override
        protected int readNext() throws ParseException {
            _previousChar = _currentChar;
            return super.readNext();
        }
        
        protected String readComment() throws ParseException {
    		assert _currentChar == '<';
    		
    		StringBuilder b = new StringBuilder("" + _currentChar);
    		int commentNestLevel = 1;
    		readNext();
    	
    		while (_currentInt >= 0 && commentNestLevel > 0) {
    			switch (_currentChar) {
    				case '>':
    					commentNestLevel--;
    					break;
    				case '<':
    					commentNestLevel++;
    					break;
    				default:
    			}
    			if (_currentInt >= 0) {
    				if (!_stripInnerComments || commentNestLevel <= 1) {
    					if (_currentChar != '>' || commentNestLevel == 0) {
    						b.append(_currentChar);
    					}
    				}
    			}
    			readNext();
    		}

    		return b.toString();
    	}


        public abstract void comment(String comment) throws ParseException;

        public abstract void value(String value) throws ParseException;
    }

}
