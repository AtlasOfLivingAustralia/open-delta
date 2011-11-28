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

public class IntkeyDirType {
	// NOTE - When adding to this list, add only to the END - otherwise you will break
	// any (internally) saved directives!!!
	    public static final int XXX_XXX_XXX_XXX = 0;
	// The following are included only for compatability with the MS-DOS version
	    public static final int HELP = 1;
	    public static final int DEFINE_FUNCTION = 2;
	    public static final int STATUS_FUNCTION = 3;
	// Now we get down to "real" directives
	    public static final int BEST = 4;
	    public static final int CHANGE = 5;
	    public static final int CHARACTERS = 6;
	    public static final int COMMENT = 7;
	    public static final int CONTENTS = 8;
	    public static final int DEFINE =9;
	    public static final int DEFINE_CHARACTERS = 10;
	    public static final int DEFINE_TAXA =11;
	    public static final int DEFINE_BUTTON = 12;
	    public static final int DEFINE_ENDIDENTIFY = 13;
	    public static final int DEFINE_NAMES =14 ;
	    public static final int DEFINE_SUBJECTS =15 ;
	    public static final int DELETe = 16;   // Last letter modified to avoid problems with Window #define DELETE
	    public static final int DESCRIBE =17 ;
	    public static final int DIAGNOSE =18 ;
	    public static final int DIFFERENCES = 19;
	    public static final int DISPLAY = 20;
	    public static final int DISPLAY_CLEAR = 21;
	    public static final int DISPLAY_COMMANDS = 22;
	    public static final int DISPLAY_COMMENTS = 23;
	    public static final int DISPLAY_CONTINUOUS = 24;
	    public static final int DISPLAY_IMAGES = 25;
	    public static final int DISPLAY_INAPPLICABLES = 26;
	    public static final int DISPLAY_INPUT = 27;
	    public static final int DISPLAY_KEYWORDS = 28;
	    public static final int DISPLAY_NUMBERING = 29;
	    public static final int DISPLAY_OUTPUT = 30;
	    public static final int DISPLAY_SCALED = 31;
	    public static final int DISPLAY_UNKNOWNS = 32;
	    public static final int EXCLUDE = 33;
	    public static final int EXCLUDE_CHARACTERS = 34;
	    public static final int EXCLUDE_TAXA = 35;
	    public static final int FILE = 36;
	    public static final int FILE_INPUT = 37;
	    public static final int FILE_OUTPUT = 38;
	    public static final int FILE_LOG = 39;
	    public static final int FILE_JOURNAL = 40;
	    public static final int FILE_CLOSE = 41;
	    public static final int FILE_CHARACTERS = 42;
	    public static final int FILE_TAXA = 43;
	    public static final int FILE_DISPLAY = 44;
	    public static final int FIND = 45;
	    public static final int FIND_TAXA = 46;
	    public static final int FIND_CHARACTERS = 47;
	    public static final int ILLUSTRATE = 48;
	    public static final int ILLUSTRATE_CHARACTERS = 49;
	    public static final int ILLUSTRATE_TAXA = 50;
	    public static final int INCLUDE = 51;
	    public static final int INCLUDE_CHARACTERS = 52;
	    public static final int INCLUDE_TAXA = 53;
	    public static final int LASTBEST = 54;
	    public static final int NEWDATASET = 55;
	    public static final int OUTPUT = 56;
	    public static final int OUTPUT_CHARACTERS = 57;
	    public static final int OUTPUT_COMMENT = 58;
	    public static final int OUTPUT_DESCRIBE = 59;
	    public static final int OUTPUT_DIAGNOSE = 60;
	    public static final int OUTPUT_DIFFERENCES = 61;
	    public static final int OUTPUT_ON = 62;
	    public static final int OUTPUT_OFF = 63;
	    public static final int OUTPUT_SIMILARITIES = 64;
	    public static final int OUTPUT_SUMMARY = 65;
	    public static final int OUTPUT_TAXA =66 ;
	    public static final int OUTPUT_TYPESETTING = 67;
	    public static final int PREFERENCES = 68;
	    public static final int QUIT = 69;
	    public static final int RESTART = 70;
	    public static final int SEPARATE = 71;
	    public static final int SET = 72;
	    public static final int SET_AUTOTAXA = 73;
	    public static final int SET_AUTOTOLERANCE = 74;
	    public static final int SET_DEMONSTRATION = 75;
	    public static final int SET_DIAGLEVEL = 76;
	    public static final int SET_DIAGTYPE = 77;
	    public static final int SET_DIAGTYPE_SPECIMENS = 78;
	    public static final int SET_DIAGTYPE_TAXA = 79;
	    public static final int SET_EXACT =80 ;
	    public static final int SET_FIX = 81;
	    public static final int SET_IMAGEPATH = 82;
	    public static final int SET_MATCH = 83;
	    public static final int SET_RBASE = 84;
	    public static final int SET_RELIABILITIES = 85;
	    public static final int SET_STOPBEST = 86;
	    public static final int SET_TOLERANCE = 87;
	    public static final int SET_VARYWT = 88;
	    public static final int SHOW = 89;
	    public static final int SIMILARITIES = 90;
	    public static final int STATUS = 91;
	    public static final int STATUS_ALL = 92;
	    public static final int STATUS_DISPLAY = 93;
	    public static final int STATUS_INCLUDE = 94;
	    public static final int STATUS_INCLUDE_CHARACTERS = 95;
	    public static final int STATUS_INCLUDE_TAXA = 96;
	    public static final int STATUS_EXCLUDE = 97;
	    public static final int STATUS_EXCLUDE_CHARACTERS = 98;
	    public static final int STATUS_EXCLUDE_TAXA = 99;
	    public static final int STATUS_FILES = 100;
	    public static final int STATUS_OUTPUT = 101;
	    public static final int STATUS_SET = 102;
	    public static final int SUMMARY = 103;
	    public static final int TAXA = 104;
	    public static final int USE = 105;
	    public static final int DISPLAY_WINDOWING = 106;
	    public static final int DISPLAY_LOG = 107;
	    public static final int SET_INFOPATH = 108;
	    public static final int DEFINE_INFORMATION = 109;
	    public static final int INFORMATION = 110;
	    public static final int DISPLAY_CHARACTERORDER = 111;
	    public static final int DISPLAY_CHARACTERORDER_BEST = 112;
	    public static final int DISPLAY_CHARACTERORDER_NATURAL = 113;
	    public static final int DISPLAY_CHARACTERORDER_SEPARATE = 114;
	    public static final int LIST_END = 115;  // Insert new directives just BEFORE this!
	    
	    public static Directive[] IntkeyDirArray = new Directive[] {
	        new Directive(new String[] {"XXX", "XXX", "XXX", "XXX"}, 0, IntkeyDirType.XXX_XXX_XXX_XXX, DirectiveArgType.DIRARG_OTHER, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Help", "", "", ""}, 0, IntkeyDirType.HELP, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Function", "", ""}, 0, IntkeyDirType.DEFINE_FUNCTION, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Function", "", ""}, 0, IntkeyDirType.STATUS_FUNCTION, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Best", "", "", ""}, 0, IntkeyDirType.BEST, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Change", "", "", ""}, 0, IntkeyDirType.CHANGE, DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Characters", "", "", ""}, 0, IntkeyDirType.CHARACTERS, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Comment", "", "", ""}, 0, IntkeyDirType.COMMENT, DirectiveArgType.DIRARG_COMMENT, new DirInComment(), new DirOutDefault()),
	        new Directive(new String[] {"Contents", "", "", ""}, 0, IntkeyDirType.CONTENTS, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "", "", ""}, 0, IntkeyDirType.DEFINE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Button", "", ""}, 0, IntkeyDirType.DEFINE_BUTTON, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Characters", "", ""}, 0, IntkeyDirType.DEFINE_CHARACTERS, DirectiveArgType.DIRARG_KEYWORD_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Endidentify", "", ""}, 0, IntkeyDirType.DEFINE_ENDIDENTIFY, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Information", "", ""}, 0, IntkeyDirType.DEFINE_INFORMATION, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Names", "", ""}, 0, IntkeyDirType.DEFINE_NAMES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Subjects", "", ""}, 0, IntkeyDirType.DEFINE_SUBJECTS, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Define", "Taxa", "", ""}, 0, IntkeyDirType.DEFINE_TAXA, DirectiveArgType.DIRARG_KEYWORD_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Delete", "", "", ""}, 0, IntkeyDirType.DELETe, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Describe", "", "", ""}, 0, IntkeyDirType.DESCRIBE, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Diagnose", "", "", ""}, 0, IntkeyDirType.DIAGNOSE, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Differences", "", "", ""}, 0, IntkeyDirType.DIFFERENCES, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "", "", ""}, 0, IntkeyDirType.DISPLAY, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "CharacterOrder", "", ""}, 0, IntkeyDirType.DISPLAY_CHARACTERORDER, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "CharacterOrder", "Best", ""}, 0, IntkeyDirType.DISPLAY_CHARACTERORDER_BEST, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "CharacterOrder", "Natural", ""}, 0, IntkeyDirType.DISPLAY_CHARACTERORDER_NATURAL, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "CharacterOrder", "Separate", ""}, 0, IntkeyDirType.DISPLAY_CHARACTERORDER_SEPARATE, DirectiveArgType.DIRARG_ITEM, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Clear", "", ""}, 0, IntkeyDirType.DISPLAY_CLEAR, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Commands", "", ""}, 0, IntkeyDirType.DISPLAY_COMMANDS, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Comments", "", ""}, 0, IntkeyDirType.DISPLAY_COMMENTS, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Continuous", "", ""}, 0, IntkeyDirType.DISPLAY_CONTINUOUS, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Images", "", ""}, 0, IntkeyDirType.DISPLAY_IMAGES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Inapplicables", "", ""}, 0, IntkeyDirType.DISPLAY_INAPPLICABLES, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Input", "", ""}, 0, IntkeyDirType.DISPLAY_INPUT, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Keywords", "", ""}, 0, IntkeyDirType.DISPLAY_KEYWORDS, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Log", "", ""}, 0, IntkeyDirType.DISPLAY_LOG, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Numbering", "", ""}, 0, IntkeyDirType.DISPLAY_NUMBERING, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Output", "", ""}, 0, IntkeyDirType.DISPLAY_OUTPUT, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Scaled", "", ""}, 0, IntkeyDirType.DISPLAY_SCALED, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Unknowns", "", ""}, 0, IntkeyDirType.DISPLAY_UNKNOWNS, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Display", "Windowing", "", ""}, 0, IntkeyDirType.DISPLAY_WINDOWING, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Exclude", "", "", ""}, 0, IntkeyDirType.EXCLUDE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Exclude", "Characters", "", ""}, 0, IntkeyDirType.EXCLUDE_CHARACTERS, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Exclude", "Taxa", "", ""}, 0, IntkeyDirType.EXCLUDE_TAXA, DirectiveArgType.DIRARG_INTKEY_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "", "", ""}, 0, IntkeyDirType.FILE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Input", "", ""}, 0, IntkeyDirType.FILE_INPUT, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Output", "", ""}, 0, IntkeyDirType.FILE_OUTPUT, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Log", "", ""}, 0, IntkeyDirType.FILE_LOG, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Journal", "", ""}, 0, IntkeyDirType.FILE_JOURNAL, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Close", "", ""}, 0, IntkeyDirType.FILE_CLOSE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Characters", "", ""}, 0, IntkeyDirType.FILE_CHARACTERS, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Taxa", "", ""}, 0, IntkeyDirType.FILE_TAXA, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"File", "Display", "", ""}, 0, IntkeyDirType.FILE_DISPLAY, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Find", "", "", ""}, 0, IntkeyDirType.FIND, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Find", "Taxa", "", ""}, 0, IntkeyDirType.FIND_TAXA, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Find", "Characters", "", ""}, 0, IntkeyDirType.FIND_CHARACTERS, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Illustrate", "", "", ""}, 0, IntkeyDirType.ILLUSTRATE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Illustrate", "Characters", "", ""}, 0, IntkeyDirType.ILLUSTRATE_CHARACTERS, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Illustrate", "Taxa", "", ""}, 0, IntkeyDirType.ILLUSTRATE_TAXA, DirectiveArgType.DIRARG_INTKEY_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Include", "", "", ""}, 0, IntkeyDirType.INCLUDE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Include", "Characters", "", ""}, 0, IntkeyDirType.INCLUDE_CHARACTERS, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Include", "Taxa", "", ""}, 0, IntkeyDirType.INCLUDE_TAXA, DirectiveArgType.DIRARG_INTKEY_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Information", "", "", ""}, 0, IntkeyDirType.INFORMATION, DirectiveArgType.DIRARG_INTKEY_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Lastbest", "", "", ""}, 0, IntkeyDirType.LASTBEST, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Newdataset", "", "", ""}, 0, IntkeyDirType.NEWDATASET, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "", "", ""}, 0, IntkeyDirType.OUTPUT, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Characters", "", ""}, 0, IntkeyDirType.OUTPUT_CHARACTERS, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Comment", "", ""}, 0, IntkeyDirType.OUTPUT_COMMENT, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Describe", "", ""}, 0, IntkeyDirType.OUTPUT_DESCRIBE, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Diagnose", "", ""}, 0, IntkeyDirType.OUTPUT_DIAGNOSE, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Differences", "", ""}, 0, IntkeyDirType.OUTPUT_DIFFERENCES, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "On", "", ""}, 0, IntkeyDirType.OUTPUT_ON, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Off", "", ""}, 0, IntkeyDirType.OUTPUT_OFF, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Similarities", "", ""}, 0, IntkeyDirType.OUTPUT_SIMILARITIES, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Summary", "", ""}, 0, IntkeyDirType.OUTPUT_SUMMARY, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Taxa", "", ""}, 0, IntkeyDirType.OUTPUT_TAXA, DirectiveArgType.DIRARG_INTKEY_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Output", "Typesetting", "", ""}, 0, IntkeyDirType.OUTPUT_TYPESETTING, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Preferences", "", "", ""}, 0, IntkeyDirType.PREFERENCES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Quit", "", "", ""}, 0, IntkeyDirType.QUIT, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Restart", "", "", ""}, 0, IntkeyDirType.RESTART, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Separate", "", "", ""}, 0, IntkeyDirType.SEPARATE, DirectiveArgType.DIRARG_INTKEY_ITEM, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "", "", ""}, 0, IntkeyDirType.SET, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Autotaxa", "", ""}, 0, IntkeyDirType.SET_AUTOTAXA, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Autotolerance", "", ""}, 0, IntkeyDirType.SET_AUTOTOLERANCE, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Demonstration", "", ""}, 0, IntkeyDirType.SET_DEMONSTRATION, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Diaglevel", "", ""}, 0, IntkeyDirType.SET_DIAGLEVEL, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Diagtype", "", ""}, 0, IntkeyDirType.SET_DIAGTYPE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Diagtype", "Specimens", ""}, 0, IntkeyDirType.SET_DIAGTYPE_SPECIMENS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Diagtype", "Taxa", ""}, 0, IntkeyDirType.SET_DIAGTYPE_TAXA, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Exact", "", ""}, 0, IntkeyDirType.SET_EXACT, DirectiveArgType.DIRARG_INTKEY_CHARLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Fix", "", ""}, 0, IntkeyDirType.SET_FIX, DirectiveArgType.DIRARG_INTKEY_ONOFF, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Imagepath", "", ""}, 0, IntkeyDirType.SET_IMAGEPATH, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Infopath", "", ""}, 0, IntkeyDirType.SET_INFOPATH, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Match", "", ""}, 0, IntkeyDirType.SET_MATCH, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Rbase", "", ""}, 0, IntkeyDirType.SET_RBASE, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Reliabilities", "", ""}, 0, IntkeyDirType.SET_RELIABILITIES, DirectiveArgType.DIRARG_INTKEY_CHARREALLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Stopbest", "", ""}, 0, IntkeyDirType.SET_STOPBEST, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Tolerance", "", ""}, 0, IntkeyDirType.SET_TOLERANCE, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Set", "Varywt", "", ""}, 0, IntkeyDirType.SET_VARYWT, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Show", "", "", ""}, 0, IntkeyDirType.SHOW, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Similarities", "", "", ""}, 0, IntkeyDirType.SIMILARITIES, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "", "", ""}, 0, IntkeyDirType.STATUS, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "All", "", ""}, 0, IntkeyDirType.STATUS_ALL, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Display", "", ""}, 0, IntkeyDirType.STATUS_DISPLAY, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Include", "", ""}, 0, IntkeyDirType.STATUS_INCLUDE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Include", "Characters", ""}, 0, IntkeyDirType.STATUS_INCLUDE_CHARACTERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Include", "Taxa", ""}, 0, IntkeyDirType.STATUS_INCLUDE_TAXA, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Exclude", "", ""}, 0, IntkeyDirType.STATUS_EXCLUDE, DirectiveArgType.DIRARG_INTKEY_INCOMPLETE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Exclude", "Characters", ""}, 0, IntkeyDirType.STATUS_EXCLUDE_CHARACTERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Exclude", "Taxa", ""}, 0, IntkeyDirType.STATUS_EXCLUDE_TAXA, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Files", "", ""}, 0, IntkeyDirType.STATUS_FILES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Output", "", ""}, 0, IntkeyDirType.STATUS_OUTPUT, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Status", "Set", "", ""}, 0, IntkeyDirType.STATUS_SET, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Summary", "", "", ""}, 0, IntkeyDirType.SUMMARY, DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Taxa", "", "", ""}, 0, IntkeyDirType.TAXA, DirectiveArgType.DIRARG_INTKEY_ITEMLIST, new DirInDefault(), new DirOutDefault()),
	        new Directive(new String[] {"Use", "", "", ""}, 0, IntkeyDirType.USE, DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES, new DirInDefault(), new DirOutDefault())
	    };
	    
		static {
			Arrays.sort(IntkeyDirArray, new Comparator<Directive>() {

				@Override
				public int compare(Directive o1, Directive o2) {
					return o1.getNumber() < o2.getNumber() ? -1 : 1;
				}
			});
		}


}
