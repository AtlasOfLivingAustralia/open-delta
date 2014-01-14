/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.model.format;

import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Utils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.StringReader;
import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Base class for DELTA formatters.
 */
public class Formatter {

    protected static Pattern EMPTY_COMMENT_PATTERN = Pattern.compile("<\\s*>");

    protected boolean _stripFormatting;
    protected boolean _capitaliseFirstWord;
    protected boolean _rtfToHtml;
    protected boolean _despaceRtf;
    
    protected CommentStrippingMode _commentStrippingMode;
    protected AngleBracketHandlingMode _angleBracketHandlingMode;
    protected String _dashReplacement;
    protected String _dashReplacementForRegexp;

    public static enum CommentStrippingMode {
        RETAIN, STRIP_ALL, RETAIN_SURROUNDING_STRIP_INNER, STRIP_INNER
    }

    public static enum AngleBracketHandlingMode {
        RETAIN, REPLACE, REMOVE, REMOVE_SURROUNDING_REPLACE_INNER, CONTEXT_SENSITIVE_REPLACE
    };

    public Formatter(CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
        this(commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord, false);
    }
    
    public Formatter(CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord, boolean rtfToHtml) {
        _commentStrippingMode = commentStrippingMode;
        _angleBracketHandlingMode = angleBracketHandlingMode;
        _stripFormatting = stripFormatting;
        _capitaliseFirstWord = capitaliseFirstWord;
        _rtfToHtml = rtfToHtml;
        _despaceRtf = false;
    }

    /**
     * If this is true, any RTF formatting will be converted to HTML 
     * equivalent and html formatting (e.g. comments <>) will be escaped.
     * @param rtfToHtml true if rtf should be converted to html during the
     * formatting operation.
     */
    public void setRtfToHtml(boolean rtfToHtml) {
    	_rtfToHtml = rtfToHtml;
    }
    public void setDespaceRtf(boolean despaceRtf) {
    	_despaceRtf = despaceRtf;
    }
    public void setDashReplacement(String replacement) {
    	_dashReplacement = replacement;
    	_dashReplacementForRegexp = replacement.replaceAll("\\\\", "\\\\\\\\");
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
    
    public String defaultFormat(String text, AngleBracketHandlingMode angleBracketMode) {
    	return defaultFormat(text, _commentStrippingMode, angleBracketMode, _stripFormatting, _capitaliseFirstWord);
    }
    
    public String defaultFormat(String text, CommentStrippingMode commentMode) {
    	return defaultFormat(text, commentMode, _angleBracketHandlingMode, _stripFormatting, _capitaliseFirstWord);
    }
    
    public String defaultFormat(String text, boolean newLinesToSpace) {
        return defaultFormat(text, _commentStrippingMode, _angleBracketHandlingMode, _stripFormatting, _capitaliseFirstWord, newLinesToSpace, _rtfToHtml);
    }
    
    public String defaultFormat(String text, boolean newLinesToSpace, boolean capitaliseFirstWord) {
        return defaultFormat(text, _commentStrippingMode, _angleBracketHandlingMode, _stripFormatting, capitaliseFirstWord, newLinesToSpace, _rtfToHtml);
    }
    
    

    public String defaultFormat(String text, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
    	return defaultFormat(text, commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord, true);
    }
    
    public String defaultFormat(String text, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord, boolean newLinesToSpace) {
    	return defaultFormat(text, commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord, newLinesToSpace, _rtfToHtml);   
    }
        
    public String defaultFormat(String text, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord, boolean newLinesToSpace, boolean rtfToHtml) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        
        if (_despaceRtf) {
        	text = Utils.despaceRtf(text, false);
        }

        if (stripFormatting) {
            text = RTFUtils.stripFormatting(text, newLinesToSpace);
        }

        text = stripComments(text, commentStrippingMode);

        if (commentStrippingMode != CommentStrippingMode.STRIP_ALL) {
            text = handleAngleBrackets(text, angleBracketHandlingMode);
        }

        // Stripping formatting can leave extra whitespace lying around
        // sometimes.
        text = text.replaceAll(" +", " ");

        if (capitaliseFirstWord) {
            text = Utils.capitaliseFirstWord(text);
        }
        

        if (rtfToHtml) {
        	text = StringEscapeUtils.escapeHtml(text);
            text = replaceDash(text);
            text = RTFUtils.rtfToHtml(text);
        }
        else {
            text = replaceDash(text);
        }

        return text.trim();
    }

    private String replaceDash(String text) {
        if (StringUtils.isNotBlank(_dashReplacement)) {
            return text.replaceAll("([0-9] *)-( *[0-9])", "$1"+_dashReplacementForRegexp+"$2");
        }
        return text;
    }

    /**
     * @param text
     * @return true if the supplied text is surrounded by a pair of angle
     *         brackets, ignoring any nested pairs of angle brackets.
     */
    private boolean textSurroundedByAngleBrackets(String text) {
    	if (StringUtils.isEmpty(text)) {
    		return false;
    	}
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
        switch (angleBracketHandlingMode) {
        case REMOVE:
            text = text.replace("<", "");
            text = text.replace(">", "");
            break;
        case REPLACE:
        case CONTEXT_SENSITIVE_REPLACE:
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

		int numBrackets = 0;
		while (text.charAt(numBrackets) == '<') {
			numBrackets++;
		}
		
		return text.substring(numBrackets, text.length()-numBrackets);

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
    					// Delete the space before the comment bracket also.
    					if (_previousChar == ' ') {
    						int lastChar = b.length()-1;
    						b.deleteCharAt(lastChar);
    					}
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
