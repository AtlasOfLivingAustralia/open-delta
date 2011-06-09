package au.org.ala.delta.directives.args;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DirectiveArgument<T> {


	private String _text;
	private String _comment;
	private BigDecimal _value;
	private T _id;
	private List<BigDecimal> _dataVect;
	

	public DirectiveArgument(T id) {
		this();
		_id = id;
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
	
	public void setId(T id) {
		_id = id;
	}
	
	public void setComment(String comment) {
		_comment = comment;
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
	
	public void setValue(BigDecimal bigDecimal) {
		_value = bigDecimal;
	}
	
	@Override
	public String toString() {
		return String.format("ArgId=%d, text=%s comment=%s value=%s, dataVect=%s", _id, _text, _comment, _value, _dataVect == null ? "null" : _dataVect);
	}
}
