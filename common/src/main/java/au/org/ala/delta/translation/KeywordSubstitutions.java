package au.org.ala.delta.translation;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks keywords that are substituted during output.
 * Keywords are identified by the @ symbol.
 * Currently only @NAME is supported.
 */
public class KeywordSubstitutions {

	public static final String NAME = "NAME";
	
	private static Map<String, String> _keywords = new HashMap<String, String>();
	
	public static void put(String keyword, String value) {
		if (!keyword.startsWith("@")) {
			keyword = "@"+keyword;
		}
		
		_keywords.put(keyword, value);
		_keywords.put(keyword.toLowerCase(), value);
	}
	
	public static String substitute(String sentence) {
		
		for (String keyword : _keywords.keySet()) {
			sentence = sentence.replaceAll(keyword, _keywords.get(keyword));
		}
		
		return sentence;
	}
	
}
