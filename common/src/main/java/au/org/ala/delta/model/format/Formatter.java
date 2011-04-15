package au.org.ala.delta.model.format;

import java.io.StringReader;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * Base class for DELTA formatters.
 */
public class Formatter {
	
	private boolean _stripComments;
	private boolean _stripFormatting;
	
	public Formatter(boolean stripComments, boolean stripFormatting) {
		_stripComments = stripComments;
		_stripFormatting = stripFormatting;
	}
	
	/**
	 * Formats the supplied text according to how this Formatter was configured on construction.
	 * @param text the text to format.
	 * @return the formatted text.
	 */
	public String defaultFormat(String text) {
	
		if (StringUtils.isEmpty(text)) {
			return "";
		}
		
		if (_stripFormatting) {
			text = RTFUtils.stripFormatting(text);
		}
		if (_stripComments) {
			text = stripComments(text);
		}
		return text;
	}
	
	
	/**
	 * Removes the comments from the supplied text.
	 * @param text the text to remove comments from.
	 * @return the text without comments.
	 */
	public String stripComments(String text) {
		CommentStripper stripper = new CommentStripper(text);
		try {
			stripper.parse();
		} catch (Exception e) {
			return text;
		}
		
		return stripper.getValue();
	}
	
	class CommentStripper extends CommentExtractor {

		private StringBuilder _value = new StringBuilder();
		
		public CommentStripper(String value) {
			super(value);
		}
		@Override
		public void comment(String comment) {}

		@Override
		public void value(String value) {
			_value.append(value.trim());
			_value.append(" ");
			
		}
		
		public String getValue() {
			return _value.toString().trim();
		}
	}
	
	public static abstract class CommentExtractor extends AbstractStreamParser {

		
		public CommentExtractor(String value) {
			super(null, new StringReader(value));
		}
		@Override
		public void parse() throws Exception {
			readNext();
			StringBuffer value = new StringBuffer();
			
			while (_currentInt >= 0) {
				
				if (_currentChar == '<') {
					
					checkValue(value.toString());
					value = new StringBuffer();
					comment(readComment());
					
				}
				if (_currentInt >= 0) {
					value.append(_currentChar);
				}
				readNext();
				
			}
			checkValue(value.toString());
		}
		
		public void checkValue(String value) throws ParseException {
			if (!StringUtils.isEmpty(value)) {
				value(value);
			}
		}
		
		public abstract void comment(String comment) throws ParseException;
		
		public abstract void value(String value) throws ParseException;
	}
	
	

}
