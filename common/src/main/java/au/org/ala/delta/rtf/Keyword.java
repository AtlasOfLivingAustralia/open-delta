package au.org.ala.delta.rtf;

import java.util.HashMap;
import java.util.Map;

public abstract class Keyword {

	public static Map<String, Keyword> KEYWORDS = new HashMap<String, Keyword>();

	public static void registerKeyword(Keyword keywordDesc) {
		KEYWORDS.put(keywordDesc.getKeyword(), keywordDesc);
	}

	static {

		// Attribute keywords (keywords that alter character/section or document attributes)
		for (CharacterAttributeType attribute : CharacterAttributeType.values()) {
			registerKeyword(new AttributeKeyword(attribute.keyword(), attribute, 1, false));
		}

		// Character literal keywords...
		registerKeyword(new CharacterKeyword("\r", '\r'));
		registerKeyword(new CharacterKeyword("\n", '\n'));
		registerKeyword(new CharacterKeyword("line", '\n'));
		registerKeyword(new CharacterKeyword("tab", '\t'));
		registerKeyword(new CharacterKeyword("page", '\f'));
		registerKeyword(new CharacterKeyword("lquote", (char) 0x2018));
		registerKeyword(new CharacterKeyword("rquote", (char) 0x2019));
		registerKeyword(new CharacterKeyword("ldblquote", (char) 0x201c));
		registerKeyword(new CharacterKeyword("rdblquote", (char) 0x201d));
		registerKeyword(new CharacterKeyword("bullet", (char) 0x2022));
		registerKeyword(new CharacterKeyword("endash", (char) 0x2013));
		registerKeyword(new CharacterKeyword("emdash", (char) 0x2014));
		registerKeyword(new CharacterKeyword("enspace", (char) 0x2002));
		registerKeyword(new CharacterKeyword("emspace", (char) 0x2003));

		// Destinations...
		registerKeyword(new DestinationKeyword("fonttbl", DestinationState.Header));
		registerKeyword(new DestinationKeyword("colortbl", DestinationState.Header));
		registerKeyword(new DestinationKeyword("info", DestinationState.Header));
		registerKeyword(new DestinationKeyword("stylesheet", DestinationState.Header));

		// Special keywords
		registerKeyword(new UnicodeKeyword("u"));
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

	public static AttributeKeyword findAttributeKeyword(CharacterAttributeType attrType) {
		for (Keyword kwd : KEYWORDS.values()) {
			if (kwd instanceof AttributeKeyword) {
				AttributeKeyword attrKwd = (AttributeKeyword) kwd;
				if (attrKwd.getAttributeType() == attrType) {
					return attrKwd;
				}
			}
		}

		return null;
	}

	public static CharacterKeyword findKeywordForCharacter(char ch) {
		for (Keyword kwd : KEYWORDS.values()) {
			if (kwd instanceof CharacterKeyword) {
				CharacterKeyword charKwd = (CharacterKeyword) kwd;
				if (charKwd.getOutputChar() == ch) {
					return charKwd;
				}
			}
		}

		return null;
	}

}
