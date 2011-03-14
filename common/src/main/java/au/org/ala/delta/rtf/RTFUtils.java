package au.org.ala.delta.rtf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RTFUtils {

	public static String stripFormatting(String rtf) {
		return filter(rtf);
	}
	
	public static String stripUnrecognizedRTF(String rtf) {
		return filter(rtf, "i", "b", "u", "super", "sub");
	}
	
	private static String filter(String rtf, String...allowedKeywords) {
		FilteringRTFHandler handler = new FilteringRTFHandler(allowedKeywords);
		RTFReader reader = new RTFReader(rtf, handler);
		try {
			reader.parse();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return handler.getFilteredText();
		
	}
		
		

}

class FilteringRTFHandler implements RTFHandler {

	private Set<String> _allowedKeywords = new HashSet<String>();

	private StringBuilder _buffer;

	public FilteringRTFHandler(String... allowed) {
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
	public void onHeaderGroup(String group) {
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

}
