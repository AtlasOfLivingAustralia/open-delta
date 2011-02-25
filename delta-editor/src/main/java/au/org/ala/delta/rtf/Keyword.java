package au.org.ala.delta.rtf;

import java.util.HashMap;
import java.util.Map;

public abstract class Keyword {
	
	public static Map<String, Keyword> KEYWORDS = new HashMap<String, Keyword>();
	
	public static void registerKeyword(Keyword keywordDesc) {
		KEYWORDS.put(keywordDesc.getKeyword(), keywordDesc);
	}
	
	static {
		registerKeyword(new CharacterKeyword("\r", '\r'));
		registerKeyword(new CharacterKeyword("\n", '\n'));		
		
		// Destinations...
		registerKeyword(new DestinationKeyword("fonttbl", DestinationState.Header));	
		registerKeyword(new DestinationKeyword("colortbl", DestinationState.Header));
		registerKeyword(new DestinationKeyword("info", DestinationState.Header));
		registerKeyword(new DestinationKeyword("stylesheet", DestinationState.Header));
	}

	protected String _keyword;
	protected KeywordType _type;

	public Keyword(String keyword, KeywordType type) {
		_keyword = keyword;
		_type = type;
	}

	public String getKeyword() {
		return _keyword;
	}
		
	public KeywordType getKeywordType() {
		return _type;
	}

}
