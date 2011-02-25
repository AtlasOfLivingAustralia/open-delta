package au.org.ala.delta.rtf;

public class DestinationKeyword extends Keyword {
	
	private DestinationState _destState;

	public DestinationKeyword(String keyword, DestinationState destState) {
		super(keyword, KeywordType.Destination);
		_destState = destState;
	}
	
	public DestinationState getDestinationState() {
		return _destState;
	}

}
