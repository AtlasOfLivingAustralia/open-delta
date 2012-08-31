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
package au.org.ala.delta.rtf;

import java.io.IOException;


/**
 * A special keyword is given access to the parser internals to perform special processing.
 */
public abstract class SpecialKeyword extends Keyword {

	public SpecialKeyword(String keyword) {
		super(keyword, KeywordType.Special);
	}
	
	/**
	 * Process the keyword, returning an array of characters to output.
	 * @param param the keyword parameter.
	 * @param reader the RTFReader - provides access to the stream being read and the current state.
	 * @return an array of characters to be output.
	 */
	public abstract char[] process(int param, RTFReader reader) throws IOException;
	
}
