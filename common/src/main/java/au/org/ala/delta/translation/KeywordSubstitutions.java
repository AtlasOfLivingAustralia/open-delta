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
