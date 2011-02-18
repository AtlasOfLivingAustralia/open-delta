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
package au.org.ala.delta.slotfile;

public class DirectiveArgType {

	public static final int DIRARG_NONE = 0; // No argument
	public static final int DIRARG_COMMENT = 1; // Comment - MAY be same as DIRARG_TEXT, unless a commented directive
	public static final int DIRARG_INTERNAL = 2; // Handled internally
	public static final int DIRARG_TRANSLATION = 3; // A "TRANSLATE INTO" - same effect as DIRARG_NONE
	public static final int DIRARG_TEXT = 4; // Text (might need further breakdown later; e.g. - mandatory vs. optional; single- vs. multi-line)
	public static final int DIRARG_FILE = 5; // Same as DIRARG_TEXT, except argument is also a filename, so RTF translation is skipped
	public static final int DIRARG_INTEGER = 6; // Single integer value
	public static final int DIRARG_REAL = 7; // Single real value
	public static final int DIRARG_CHAR = 8; // Single character
	public static final int DIRARG_CHARLIST = 9; // Set of characters
	public static final int DIRARG_ITEM = 10; // Single item
	public static final int DIRARG_ITEMLIST = 11; // Set of items
	public static final int DIRARG_TEXTLIST = 12; // Set of (number + text)
	public static final int DIRARG_CHARTEXTLIST = 13; // Set of (character + text)
	public static final int DIRARG_CHARINTEGERLIST = 14; // Set of (character + integer value)
	public static final int DIRARG_CHARREALLIST = 15; // Set of (character + real value)
	public static final int DIRARG_CHARGROUPS = 16; // Set of (character groups)
	public static final int DIRARG_ITEMREALLIST = 17; // Set of (item + real value)
	public static final int DIRARG_ITEMTEXTLIST = 18; // Set of (item + text)
	public static final int DIRARG_ITEMFILELIST = 19; // Same as DIRARG_ITEMTEXTLIST, except text is also a filename, so RTF translation is skipped
	public static final int DIRARG_ITEMCHARLIST = 20; // Set of (item + (set of character))
	public static final int DIRARG_KEYSTATE = 21; // Key state info
	public static final int DIRARG_ALLOWED = 22; // Set of (character + 3 real)
	public static final int DIRARG_PRESET = 23; // Set of (character + column + group)
	public static final int DIRARG_OTHER = 24; // Stuff I haven't yet dealt with - same effect as DIRARG_TEXT
	public static final int DIRARG_INTKEY_ONOFF = 25; // Either off (-1), on (1), or not specified (0)
	public static final int DIRARG_INTKEY_ITEM = 26; // Single (item or keyword)
	public static final int DIRARG_INTKEY_CHARLIST = 27; // Set of (character or keyword)
	public static final int DIRARG_INTKEY_ITEMLIST = 28; // Set of (item or keyword)
	public static final int DIRARG_KEYWORD_CHARLIST = 29;// Keyword + set of (character or keyword)
	public static final int DIRARG_KEYWORD_ITEMLIST = 30;// Keyword + set of (item or keyword)
	public static final int DIRARG_INTKEY_CHARREALLIST = 31; // Set of ((character or keyword)+ real value)
	public static final int DIRARG_INTKEY_ITEMCHARSET = 32; // Set of (item or item keyword) + set of (char or char keyword)
	public static final int DIRARG_INTKEY_ATTRIBUTES = 33; // Set of ((character or keyword) + attribute-like values)
	public static final int DIRARG_INTKEY_INCOMPLETE = 34; // An incomplete Intkey directive
	public static final int DIRARG_LISTEND = 35; // Dummy value to keep at end of list;
	
	

}
