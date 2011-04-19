package au.org.ala.delta.model;

public class TypeSettingMark {

	private int _markNumber;
	private String _mark;
	private boolean _allowLineBreaks;
	
	public TypeSettingMark(int markNumber, String mark, boolean allowLineBreaks) {
		_markNumber = markNumber;
		_mark = mark;
		_allowLineBreaks = allowLineBreaks;
	}
	
	public int getMarkNumber() {
		return _markNumber;
	}
	
	public String getMark() {
		return _mark;
	}
	
	public boolean getAllowLineBreaks() {
		return _allowLineBreaks;
	}
}
