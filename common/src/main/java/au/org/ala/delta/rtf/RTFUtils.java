package au.org.ala.delta.rtf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class RTFUtils {

	public static String stripFormatting(String rtf, boolean newlinesToSpace) {
		return filter(rtf, newlinesToSpace);
	}

	public static String stripFormatting(String rtf) {
		return filter(rtf, true);
	}

	public static String stripUnrecognizedRTF(String rtf) {
		return filter(rtf, true, "i", "b", "u", "super", "sub");
	}

	public static String stripUnrecognizedRTF(String rtf, boolean newlinesToSpace) {
		return filter(rtf, newlinesToSpace, "i", "b", "u", "super", "sub");
	}

	private static String filter(String rtf, boolean newLinesToSpace, String... allowedKeywords) {

		if (StringUtils.isEmpty(rtf)) {
			return rtf;
		}

		FilteringRTFHandler handler = new FilteringRTFHandler(newLinesToSpace, allowedKeywords);
		RTFReader reader = new RTFReader(rtf, handler);
		try {
			reader.parse();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			// Ignore, and return the original text
			return rtf;
		}
		return handler.getFilteredText();
	}
	
	/**
	 * Escape RTF special characters in the supplied string
	 * @param rtf
	 * @return
	 */
	public static String escapeRTF(String rtf) {
	    String escapedRTF = rtf.replace("\\", "\\\\");
	    escapedRTF = escapedRTF.replace("{", "\\{");
	    escapedRTF = escapedRTF.replace("}", "\\}");
	    return escapedRTF;
	}
	
	/**
	 * Returns the index of the first non-RTF keyword character after the 
	 * specified index.
	 * @param text the text to check.
	 * @param startPos the position in the text to start checking.
	 */
	public static int skipKeyword(String text, int startPos) {
		FirstRTFKeywordMarker handler = new FirstRTFKeywordMarker();
		RTFReader reader = new RTFReader(text.substring(startPos), handler);
		try {
			reader.parse();
		} catch (Exception ex) {
			// Ignore, and return the original text
			return -1;
		}
		
		return text.indexOf(handler.getFilteredText());
	}
	
	/**
	 * Converts RTF formatted text into the html equivalent.
	 * @param rtf the RTF text to convert.
	 * @return the text with RTF control words replaced with the equivalent
	 * HTML.
	 */
	public static String rtfToHtml(String rtf) {
		RtfToHtmlConverter converter = new RtfToHtmlConverter();
		
		RTFReader reader = new RTFReader(rtf, converter);
		try {
			reader.parse();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			// Ignore, and return the original text
			return rtf;
		}
		return converter.getText();
		
	}

}

class FirstRTFKeywordMarker extends FilteringRTFHandler {
	
	private boolean _keywordEncountered;
	private int _secondKeyWordStart = -1;
	
	public FirstRTFKeywordMarker() {
		super(false);
	}
	
	@Override
	public void onKeyword(String keyword, boolean hasParam, int param) {
		markKeyword();
	}

	protected void markKeyword() {
		if (_keywordEncountered && _secondKeyWordStart == -1) {
			_secondKeyWordStart = _buffer.length();
		}
		_keywordEncountered = true;
	}
	
	@Override
	public void onCharacterAttributeChange(List<AttributeValue> values) {
		markKeyword();
	}

	@Override
	public void onParagraphAttributeChange(List<AttributeValue> values) {
		markKeyword();
	}
	
	public String getFilteredText() {
		String result = _buffer.toString();
		if (_secondKeyWordStart >= 0) {
			result = result.substring(0, _secondKeyWordStart);
		}
		return result;
	}
	
}

class FilteringRTFHandler implements RTFHandler {

	private Set<String> _allowedKeywords = new HashSet<String>();
	
	protected StringBuilder _buffer;
	private boolean _newlinesToSpace;

	public FilteringRTFHandler(boolean newlinesToSpace, String... allowed) {
		_newlinesToSpace = newlinesToSpace;
		for (String word : allowed) {
			_allowedKeywords.add(word);
		}
		_buffer = new StringBuilder();
	}

	@Override
	public void startParse() {
	}

	@Override
	public void onKeyword(String keyword, boolean hasParam, int param) {

		if (_allowedKeywords.contains(keyword)) {
			_buffer.append("\\").append(keyword);
			if (hasParam) {
				_buffer.append(param);
			}
			_buffer.append(" ");
		}
	}

	@Override
	public void onHeaderGroup(String keyword, String content) {
	}

	@Override
	public void onTextCharacter(char ch) {
		_buffer.append(ch);
	}

	@Override
	public void endParse() {
	}

	public String getFilteredText() {
		return _buffer.toString();
	}

	@Override
	public void onCharacterAttributeChange(List<AttributeValue> values) {
		handleAttributeChange(values);
	}

	@Override
	public void onParagraphAttributeChange(List<AttributeValue> values) {
		handleAttributeChange(values);
	}

	private void handleAttributeChange(List<AttributeValue> values) {
		boolean atLeastOneAllowed = false;
		for (AttributeValue val : values) {
			if (_allowedKeywords.contains(val.getKeyword())) {
				atLeastOneAllowed = true;
				_buffer.append("\\").append(val.getKeyword());
				if (val.hasParam()) {
					_buffer.append(val.getParam());
				}
			}
		}
		if (atLeastOneAllowed) {
			_buffer.append(" "); // terminate the string of control words...
		}
	}

	@Override
	public void startParagraph() {
	}

	@Override
	public void endParagraph() {
		if (_newlinesToSpace) {
			_buffer.append(" ");
		}
		else {
			_buffer.append("\n");
		}
	}

}
