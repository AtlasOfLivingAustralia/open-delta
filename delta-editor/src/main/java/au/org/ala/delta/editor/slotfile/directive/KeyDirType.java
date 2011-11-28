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
package au.org.ala.delta.editor.slotfile.directive;

import java.util.Arrays;
import java.util.Comparator;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;

public class KeyDirType {
	public static final int XXX_XXX_XXX_XXX = 0;
	public static final int ABASE = 1;
	public static final int ADD_CHARACTER_NUMBERS = 2;
	public static final int ALLOW_IMPROPER_SUBGROUPS = 3;
	public static final int CHARACTER_RELIABILITIES = 4;
	public static final int CHARACTERS_FILE = 5;
	public static final int COMMENT = 6;
	public static final int DUMP = 7;
	public static final int EXCLUDE_CHARACTERS = 8;
	public static final int EXCLUDE_ITEMS = 9;
	public static final int HEADING = 10;
	public static final int INCLUDE_CHARACTERS = 11;
	public static final int INCLUDE_ITEMS = 12;
	public static final int INSERT_TYPESETTING_MARKS = 13;
	public static final int ITEM_ABUNDANCES = 14;
	public static final int ITEMS_FILE = 15;
	public static final int KEY_OUTPUT_FILE = 16;
	public static final int KEY_TYPESETTING_FILE = 17;
	public static final int LISTING_FILE = 18;
	public static final int MATRIX_DUMP = 19;
	public static final int NO_BRACKETTED_KEY = 20;
	public static final int NO_TABULAR_KEY = 21;
	public static final int NUMBER_OF_CONFIRMATORY_CHARACTERS = 22;
	public static final int OMIT_TYPESETTING_MARKS = 23;
	public static final int PAGE_LENGTH = 24;
	public static final int PRESET_CHARACTERS = 25;
	public static final int PRINT_WIDTH = 26;
	public static final int RBASE = 27;
	public static final int REUSE = 28;
	public static final int STOP_AFTER_COLUMN = 29;
	public static final int STORAGE_FACTOR = 30;
	public static final int TREAT_CHARACTERS_AS_VARIABLE = 31;
	public static final int TREAT_UNKNOWN_AS_INAPPLICABLE = 32;
	public static final int TRUNCATE_TABULAR_KEY_AT = 33;
	public static final int TYPESETTING_STYLE = 34;
	public static final int TYPESETTING_TABS = 35;
	public static final int VARYWT = 36;
	public static final int OUTPUT_FORMAT_RTF = 37;
	public static final int OUTPUT_FORMAT_HTML = 38;
	public static final int TYPESETTING_MARKS = 39;
	public static final int INPUT_FILE = 40;
	public static final int PRINT_COMMENT = 41;
	public static final int OUTPUT_DIRECTORY = 42;
	public static final int LIST_END = 43; // Insert new directives just BEFORE this!
	
	public static Directive[] KeyDirArray = new Directive[] {
        new Directive(new String[] {"XXX", "XXX", "XXX", "XXX"}, 0, KeyDirType.XXX_XXX_XXX_XXX, DirectiveArgType.DIRARG_OTHER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ABASE", "", "", ""}, 0, KeyDirType.ABASE, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ADD", "CHARACTER", "NUMBERS", ""}, 0, KeyDirType.ADD_CHARACTER_NUMBERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ALLOW", "IMPROPER", "SUBGROUPS", ""}, 0, KeyDirType.ALLOW_IMPROPER_SUBGROUPS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "RELIABILITIES", "", ""}, 0, KeyDirType.CHARACTER_RELIABILITIES, DirectiveArgType.DIRARG_CHARREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTERS", "FILE", "", ""}, 0, KeyDirType.CHARACTERS_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"COMMENT", "", "", ""}, 0, KeyDirType.COMMENT, DirectiveArgType.DIRARG_COMMENT, new DirInComment(), new DirOutDefault()),
        new Directive(new String[] {"DUMP", "", "", ""}, 0, KeyDirType.DUMP, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EXCLUDE", "CHARACTERS", "", ""}, 0, KeyDirType.EXCLUDE_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EXCLUDE", "ITEMS", "", ""}, 0, KeyDirType.EXCLUDE_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"HEADING", "", "", ""}, 0, KeyDirType.HEADING, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INCLUDE", "CHARACTERS", "", ""}, 0, KeyDirType.INCLUDE_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INCLUDE", "ITEMS", "", ""}, 0, KeyDirType.INCLUDE_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INPUT", "FILE", "", ""}, 0, KeyDirType.INPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInInputFile(), new DirOutDefault()),
        new Directive(new String[] {"INSERT", "TYPESETTING", "MARKS", ""}, 0, KeyDirType.INSERT_TYPESETTING_MARKS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEM", "ABUNDANCES", "", ""}, 0, KeyDirType.ITEM_ABUNDANCES, DirectiveArgType.DIRARG_ITEMREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEMS", "FILE", "", ""}, 0, KeyDirType.ITEMS_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "OUTPUT", "FILE", ""}, 0, KeyDirType.KEY_OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "TYPESETTING", "FILE", ""}, 0, KeyDirType.KEY_TYPESETTING_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LISTING", "FILE", "", ""}, 0, KeyDirType.LISTING_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"MATRIX", "DUMP", "", ""}, 0, KeyDirType.MATRIX_DUMP, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NO", "BRACKETTED", "KEY", ""}, 0, KeyDirType.NO_BRACKETTED_KEY, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NO", "TABULAR", "KEY", ""}, 0, KeyDirType.NO_TABULAR_KEY, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NUMBER", "OF", "CONFIRMATORY", "CHARACTERS"}, 0, KeyDirType.NUMBER_OF_CONFIRMATORY_CHARACTERS, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "TYPESETTING", "MARKS", ""}, 0, KeyDirType.OMIT_TYPESETTING_MARKS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "DIRECTORY", "", ""}, 0, KeyDirType.OUTPUT_DIRECTORY, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "FORMAT", "HTML", ""}, 0, KeyDirType.OUTPUT_FORMAT_HTML, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "FORMAT", "RTF", ""}, 0, KeyDirType.OUTPUT_FORMAT_RTF, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PAGE", "LENGTH", "", ""}, 0, KeyDirType.PAGE_LENGTH, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRESET", "CHARACTERS", "", ""}, 0, KeyDirType.PRESET_CHARACTERS, DirectiveArgType.DIRARG_PRESET, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "COMMENT", "", ""}, 0, KeyDirType.PRINT_COMMENT, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "WIDTH", "", ""}, 0, KeyDirType.PRINT_WIDTH, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"RBASE", "", "", ""}, 0, KeyDirType.RBASE, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REUSE", "", "", ""}, 0, KeyDirType.REUSE, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"STOP", "AFTER", "COLUMN", ""}, 0, KeyDirType.STOP_AFTER_COLUMN, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"STORAGE", "FACTOR", "", ""}, 0, KeyDirType.STORAGE_FACTOR, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TREAT", "CHARACTERS", "AS", "VARIABLE"}, 0, KeyDirType.TREAT_CHARACTERS_AS_VARIABLE, DirectiveArgType.DIRARG_ITEMCHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TREAT", "UNKNOWN", "AS", "INAPPLICABLE"}, 0, KeyDirType.TREAT_UNKNOWN_AS_INAPPLICABLE, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRUNCATE", "TABULAR", "KEY", "AT"}, 0, KeyDirType.TRUNCATE_TABULAR_KEY_AT, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TYPESETTING", "MARKS", "", ""}, 0, KeyDirType.TYPESETTING_MARKS, DirectiveArgType.DIRARG_TEXTLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TYPESETTING", "STYLE", "", ""}, 0, KeyDirType.TYPESETTING_STYLE, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TYPESETTING", "TABS", "", ""}, 0, KeyDirType.TYPESETTING_TABS, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"VARYWT", "", "", ""}, 0, KeyDirType.VARYWT, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault())
    };
	
	static {
		Arrays.sort(KeyDirArray, new Comparator<Directive>() {

			@Override
			public int compare(Directive o1, Directive o2) {
				return o1.getNumber() < o2.getNumber() ? -1 : 1;
			}
		});
	}
	

}
