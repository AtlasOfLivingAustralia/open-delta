package au.org.ala.delta.rtf;

public class AttributeValue {
	
	private String _keyword;
	private boolean _hasParam;
	private int _param;
	
	public AttributeValue(String keyword, boolean hasParam, int param) {
		_keyword = keyword;
		_param = param;
		_hasParam = hasParam;
	}
	
	public String getKeyword() {
		return _keyword;
	}
	
	public int getParam() {		
		return _param;
	}
	
	public boolean hasParam() {
		return _hasParam;
	}

}
