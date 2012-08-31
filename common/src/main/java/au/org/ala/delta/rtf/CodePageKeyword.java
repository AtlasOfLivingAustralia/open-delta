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

public class CodePageKeyword extends SpecialKeyword {
	
	public CodePageKeyword() {
		super("'");
	}

	@Override
	public char[] process(int param, RTFReader reader) throws IOException {
		char[] hex = new char[2];
		hex[0] = (char) reader.read();
		hex[1] = (char) reader.read();
		
		int value = Integer.parseInt(new String(hex), 16);
		return new char[]  { (char) value };		
	}

}
