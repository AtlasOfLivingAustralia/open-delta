package au.org.ala.delta.rtf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
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
			throw new RuntimeException(ex);
		}
		return handler.getFilteredText();
	}

}

class FilteringRTFHandler implements RTFHandler {

	private Set<String> _allowedKeywords = new HashSet<String>();

	private StringBuilder _buffer;
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
    }

}
