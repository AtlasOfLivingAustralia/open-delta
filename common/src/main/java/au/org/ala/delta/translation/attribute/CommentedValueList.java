package au.org.ala.delta.translation.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentedValueList {

	private String _characterComment;
	private List<CommentedValues> _commentedValues;

	public CommentedValueList(String comment, List<CommentedValues> values) {
		_characterComment = comment;
		_commentedValues = values;
	}
	
	public String getCharacterComment() {
		return _characterComment;
	}
	
	public List<CommentedValues> getCommentedValues() {
		return _commentedValues;
	}
	
	public static class CommentedValues {
		private String _comment;
		private Values _values;
		
		public CommentedValues(Values values, String comment) {
			_values = values;
			_comment = comment;
		}
		
		public String getComment() {
			return _comment;
		}
		
		public Values getValues() {
			return _values;
		}
		
		public String getValue(int index) {
			if (_values == null) {
				throw new IndexOutOfBoundsException();
			}
			return getValues().getValue(index);
		}
		
		public int getNumValues() {
			if (_values == null) {
				return 0;
			}
			return getValues().getNumValues();
		}
	}
	
	public static class Values {
		private List<String> _values;
		private String _separator;
		
		public Values(String value) {
			_separator = "";
			_values = new ArrayList<String>();
			_values.add(value);
		}
		
		public Values(String[] values, String separator) {
			_separator = separator;
			_values = new ArrayList<String>(Arrays.asList(values));
			
		}
		
		public Values(List<String> values, String separator) {
			_values = values;
			_separator = separator;
		}
		
		public List<String> getValues() {
			return _values;
		}
		
		public String getValue(int index) {
			return _values.get(index);
		}
		
		public String getSeparator() {
			return _separator;
		}
		
		public int getNumValues() {
			return _values.size();
		}
	}
	
	
}
