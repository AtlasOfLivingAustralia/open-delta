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
package au.org.ala.delta.editor.slotfile;

public class CharType {
	public static final int UNKNOWN = 0;
	public static final int UNORDERED = 1;
	public static final int ORDERED = 2;
	public static final int INTEGER = 3;
	public static final int REAL = 4;
	public static final int TEXT = 5;
	public static final int LIST = 6;
	public static final int CYCLIC = 7;
	public static final int LISTEND = 8;
	
	public static boolean isNumeric(int type) {
		return (type == CharType.INTEGER || type == CharType.REAL);
	}

	public static boolean isMultistate(int type) {
		return (type == CharType.UNORDERED || type == CharType.ORDERED || type == CharType.LIST || type == CharType.CYCLIC);
	}

	public static boolean isText(int type) {
		return (type == CharType.TEXT);
	}

}
