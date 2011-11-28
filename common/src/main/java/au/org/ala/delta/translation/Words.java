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

public class Words {

	public enum Word {
		OR, TO, AND, VARIABLE, UNKNOWN, NOT_APPLICABLE, VARIANT, NOT_CODED, NEVER, MINIMUM, 
		MAXIMUM, UP_TO, OR_MORE, FULL_STOP, COMMA, ALTERNATE_COMMA, SEMICOLON, FULL_STOP_AGAIN, RANGE};
	
	
	private static String[] _vwords = {
			"or", "to", "and", "variable", "unknown", "not applicable", "(variant)", "not coded",
			"never", "minimum", "maximum", "up to", "or more", ".", ",", ",", ";", ".", "-"};
	
	
	public static String word(Word word) {
		return _vwords[word.ordinal()];
	}
	
	public static void setWord(Word word, String value) {
		_vwords[word.ordinal()] = value;
	}
	
}
