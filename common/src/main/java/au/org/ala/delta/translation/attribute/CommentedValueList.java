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
		
		private String _prefix;
		private String _suffix;
		
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

		public String getPrefix() {
			return _prefix;
		}

		public void setPrefix(String prefix) {
			this._prefix = prefix;
		}

		public String getSuffix() {
			return _suffix;
		}

		public void setSuffix(String suffix) {
			this._suffix = suffix;
		}
		
		
	}
	
	
}
