package au.org.ala.delta.model;

public class DeltaParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private int _lineNumber;
	private int _position;
	
	public DeltaParseException(String message) {
		super(message);		
	}
	
	public DeltaParseException(String message, int position) {
		super(message);		
		_position = position;
	}
	
	public DeltaParseException(String message, int position, int lineNumber) {
		super(message);		
		_position = position;
		_lineNumber = lineNumber;
	}
	
	public int getPosition() {
		return _position;
	}
	
	public int getLineNumber() {
		return _lineNumber;
	}
	

}
