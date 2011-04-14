package au.org.ala.delta.translation.attribute;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.format.Formatter.CommentExtractor;
import au.org.ala.delta.translation.attribute.ParsedAttribute.CommentedValues;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

public class AttributeParser {

	private static final String OR_SEPARATOR = "/";
	private static final String AND_SEPARATOR = "&";
	private static final String RANGE_SEPARATOR = "-";
	
	private String _comment;
	private String _value;
	
	public ParsedAttribute parse(String attribute) {
		
		split(new FirstCommentSplitter(attribute));
		String characterComment = _comment;
		
		String[] commentedValueStrings = _value.split(OR_SEPARATOR);
		
		List<CommentedValues> commentedValueList = new ArrayList<CommentedValues>();
		for (String commentedValue : commentedValueStrings) {
			split(new LastCommentSplitter(commentedValue));
			
			Values values = values(_value);
			CommentedValues commentedValues = new CommentedValues(values, _comment);
			commentedValueList.add(commentedValues);
			
		}
		
		ParsedAttribute parsedAttribute = new ParsedAttribute(characterComment, commentedValueList);
		return parsedAttribute;
	}
	
	private Values values(String valuesString) {
		if (StringUtils.isEmpty(valuesString)) {
			return null;
		}
		Values values;
		String separator = "";
		if (valuesString.indexOf(RANGE_SEPARATOR) > 0) {
			separator = RANGE_SEPARATOR;
		}
		if (valuesString.indexOf(AND_SEPARATOR) > 0) {
			separator = AND_SEPARATOR;
		}
		if ("".equals(separator)) {
			values = new Values(valuesString);
		}
		else {
			values = new Values(valuesString.split(separator), separator);
		}
		return values;
	}
	
	private void split(CommentSplitter splitter) {
		
		try {
			splitter.parse();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		_comment = splitter.getComment();
		_value = splitter.getValue();
	}
	
	abstract class CommentSplitter extends CommentExtractor {

		protected String _comment = "";
		protected StringBuilder _value = new StringBuilder();
		
		public CommentSplitter(String value) {
			super(value);
		}
		
		
		public String getComment() {
			return _comment;
		}
		
		public String getValue() {
			return _value.toString();
		}
		
	}
	
	class FirstCommentSplitter extends CommentSplitter {
		

		public FirstCommentSplitter(String value) {
			super(value);
		}
		
		
		@Override
		public void comment(String comment) throws ParseException {
			// only want the first leading comment
			if (StringUtils.isNotEmpty(comment) && _value.length() == 0) {
				_comment = comment;
			}
			else {
				_value.append(comment);
			}
		}

		@Override
		public void value(String value) throws ParseException {
			if (StringUtils.isNotEmpty(value))  {
				_value.append(value);
			}
		}
	}
	
	class LastCommentSplitter extends CommentSplitter {
		
		public LastCommentSplitter(String value) {
			super(value);
		}
		
		@Override
		public void comment(String comment) throws ParseException {
			// A comment with no value is invalid.
			if (_value.length() == 0) {
				throw new RuntimeException("Value missing!");
			}
			if (StringUtils.isNotEmpty(_comment)) {
				throw new ParseException("Multiple attribute comments not supported.", _position);
			}
			
			_comment = comment;
		}

		@Override
		public void value(String value) throws ParseException {
			
			if (_value.length() != 0) {
				throw new ParseException("Multiple values not supported!", _position);
			}
			
			_value.append(value);
			
		}
	}
	
}
