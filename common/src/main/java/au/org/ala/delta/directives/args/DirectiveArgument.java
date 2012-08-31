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
package au.org.ala.delta.directives.args;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DirectiveArgument<T> implements Comparable<DirectiveArgument<?>>{


	private String _text;
	private String _comment = "";
	private BigDecimal _value;
	private T _id;
	private List<BigDecimal> _dataVect;
	private String _attributeText;

	public DirectiveArgument(T id) {
		this();
		_id = id;
	}
	
	public DirectiveArgument(DirectiveArgument<T> arg) {
		this();
		_id = arg.getId();
		_comment = arg.getComment();
		if (_comment == null) {
			_comment = "";
		}
		_value = arg.getValue();
		for (BigDecimal value : arg.getData()) {
			_dataVect.add(value);
		}
	}
	
	public DirectiveArgument() {
		_dataVect = new ArrayList<BigDecimal>();
	}
		
	public void setText(String text) {
		_text = text;
	}
	
	public String getText() {
		return _text;
	}
	
	public void setValue(int value) {
		_value = new BigDecimal(value);
	}
	
	public BigDecimal getValue() {
		return _value;
	}
	
	public int getValueAsInt() {
		return _value.intValue();
	}
	
	public void setId(T id) {
		_id = id;
	}
	
	public void setComment(String comment) {
		if (_comment == null) {
			_comment = "";
		}
		else {
			_comment = comment;
		}
	}
	
	public String getComment() {
		
		return _comment;
	}
	
	public T getId() {
		return _id;
	}
	
	public void add(int value) {
		_dataVect.add(new BigDecimal(value));
	}
	
	public void add(BigDecimal value) {
		_dataVect.add(value);
	}
	
	public void add(float value) {
		_dataVect.add(new BigDecimal(value));
	}
	
	public void setValue(BigDecimal bigDecimal) {
		_value = bigDecimal;
	}
	
	public List<Integer> getDataList() {
		
		List<Integer> values = new ArrayList<Integer>(_dataVect.size());
		for (BigDecimal value : _dataVect) {
			values.add(value.intValue());
		}
		return values;
	}
	
	public List<BigDecimal> getData() {
		return _dataVect;
	}
	
	public String valueAsString() {
		if (_value != null) {
			return _value.toString();
		}
		else {
			return _text;
		}
	}
	
	public void setAttributeText(String attribute) {
		_attributeText = attribute;
	}
	
	public String getAttributeText() {
		return _attributeText;
	}
	
	@Override
	public String toString() {
		return String.format("ArgId=%d, text=%s comment=%s value=%s, dataVect=%s", _id, _text, _comment, _value, _dataVect == null ? "null" : _dataVect);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareTo(DirectiveArgument<?> o) {
		// We can get away with this case cause T will be an Integer or a String. 
		if (_id == null) {
			return -1;
		}
		if (o.getId() == null) {
			return 1;
		}
		return ((Comparable)_id).compareTo((Comparable)o.getId());
	}

	
}
