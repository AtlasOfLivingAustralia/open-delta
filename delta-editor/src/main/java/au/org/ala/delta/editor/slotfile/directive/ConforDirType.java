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

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.ApplicableCharacters;
import au.org.ala.delta.directives.CharacterForTaxonImages;
import au.org.ala.delta.directives.CharacterImages;
import au.org.ala.delta.directives.CharacterList;
import au.org.ala.delta.directives.CharacterNotes;
import au.org.ala.delta.directives.CharacterReliabilities;
import au.org.ala.delta.directives.CharacterTypes;
import au.org.ala.delta.directives.CharacterWeights;
import au.org.ala.delta.directives.Comment;
import au.org.ala.delta.directives.DataBufferSize;
import au.org.ala.delta.directives.DependentCharacters;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.Heading;
import au.org.ala.delta.directives.ImplicitValues;
import au.org.ala.delta.directives.InapplicableCharacters;
import au.org.ala.delta.directives.InputFile;
import au.org.ala.delta.directives.InsertImplicitValues;
import au.org.ala.delta.directives.ItemDescriptions;
import au.org.ala.delta.directives.ItemSubHeadings;
import au.org.ala.delta.directives.MandatoryCharacters;
import au.org.ala.delta.directives.MaximumNumberOfItems;
import au.org.ala.delta.directives.MaximumNumberOfStates;
import au.org.ala.delta.directives.NewParagraphAtCharacters;
import au.org.ala.delta.directives.NumberOfCharacters;
import au.org.ala.delta.directives.NumbersOfStates;
import au.org.ala.delta.directives.OmitCharacterNumbers;
import au.org.ala.delta.directives.OmitInapplicables;
import au.org.ala.delta.directives.OmitInnerComments;
import au.org.ala.delta.directives.OmitTypeSettingMarks;
import au.org.ala.delta.directives.OutputFormatHtml;
import au.org.ala.delta.directives.OverlayFonts;
import au.org.ala.delta.directives.PrintFile;
import au.org.ala.delta.directives.PrintWidth;
import au.org.ala.delta.directives.ReplaceAngleBrackets;
import au.org.ala.delta.directives.Show;
import au.org.ala.delta.directives.TaxonImages;
import au.org.ala.delta.directives.TypeSettingMarks;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;

public class ConforDirType {
	
	public static final int XXX_XXX_XXX_XXX = 0;
	public static final int COMMENT = 1;
	public static final int SHOW = 2;
	public static final int DATA_LISTING = 3;
	public static final int NO_DATA_LISTING = 4;
	public static final int ERROR_FILE = 5;
	public static final int LISTING_FILE = 6;
	public static final int PRINT_FILE = 7;
	public static final int OUTPUT_FILE = 8;
	public static final int INPUT_DELTA_FILE = 9;
	public static final int PAGE_LENGTH = 10;
	public static final int INPUT_FILE = 11;
	public static final int KEY_CHARACTERS_FILE = 12;
	public static final int OUTPUT_PARAMETERS = 13;
	public static final int PRINT_WIDTH = 14;
	public static final int OUTPUT_WIDTH = 15;
	public static final int KEY_ITEMS_FILE = 16;
	public static final int PREVIOUS_INPUT_FILE = 17;
	public static final int NEW_LISTING_PAGE = 18;
	public static final int NEW_PRINT_PAGE = 19;
	public static final int END = 20;
	public static final int HEADING = 21;
	public static final int PRINT_COMMENT = 22;
	public static final int LIST_HEADING = 23;
	public static final int PRINT_HEADING = 24;
	public static final int DIST_ITEMS_FILE = 25;
	public static final int KEY_OUTPUT_FILE = 26;
	public static final int DIST_OUTPUT_FILE = 27;
	public static final int INTKEY_OUTPUT_FILE = 28;
	public static final int PRINT_APPEND = 29;
	public static final int REGISTRATION_HEADING = 30;
	public static final int REGISTRATION_SUBHEADING = 31;
	public static final int REGISTRATION_VALIDATION = 32;
	public static final int DATA_BUFFER_SIZE = 33;
	public static final int MAXIMUM_NUMBER_OF_ITEMS = 34;
	public static final int MAXIMUM_NUMBER_OF_STATES = 35;
	public static final int NUMBER_OF_CHARACTERS = 36;
	public static final int STATE_CODES = 37;
	public static final int CHARACTER_TYPES = 38;
	public static final int NUMBERS_OF_STATES = 39;
	public static final int CHARACTER_RELIABILITIES = 40;
	public static final int CHARACTER_WEIGHTS = 41;
	public static final int EXCLUDE_CHARACTERS = 42;
	public static final int INCLUDE_CHARACTERS = 43;
	public static final int DECIMAL_PLACES = 44;
	public static final int ITEM_ABUNDANCES = 45;
	public static final int ITEM_WEIGHTS = 46;
	public static final int EXCLUDE_ITEMS = 47;
	public static final int INCLUDE_ITEMS = 48;
	public static final int KEY_STATES = 49;
	public static final int INSERT_CHARACTER_SEQUENCE_NUMBERS = 50;
	public static final int TAXON_IMAGES = 51;
	public static final int NEW_PARAGRAPHS_AT_CHARACTERS = 52;
	public static final int OMIT_CHARACTER_NUMBERS = 53;
	public static final int PRINT_ITEM_NAMES = 54;
	public static final int SPECIAL_STORAGE = 55;
	public static final int TRANSLATE_INTO_ALICE_FORMAT = 56;
	public static final int TRANSLATE_INTO_DELTA_FORMAT = 57;
	public static final int TRANSLATE_INTO_DCR_FORMAT = 58;
	public static final int TRANSLATE_INTO_DIST_FORMAT = 59;
	public static final int TRANSLATE_INTO_HENNIG86_FORMAT = 60;
	public static final int TRANSLATE_INTO_INTKEY_FORMAT = 61;
	public static final int TRANSLATE_INTO_KEY_FORMAT = 62;
	public static final int TRANSLATE_INTO_NATURAL_LANGUAGE = 63;
	public static final int TRANSLATE_INTO_NEXUS_FORMAT = 64;
	public static final int TRANSLATE_INTO_PAUP_FORMAT = 65;
	public static final int TRANSLATE_INTO_PAYNE_FORMAT = 66;
	public static final int TREAT_UNKNOWN_AS_VARIABLE = 67;
	public static final int NUMBER_STATES_FROM_ZERO = 68;
	public static final int PRINT_CHARACTER_LIST = 69;
	public static final int PRINT_ITEM_DESCRIPTIONS = 70;
	public static final int OMIT_PERIOD_FOR_CHARACTERS = 71;
	public static final int OMIT_TYPESETTING_MARKS = 72;
	public static final int IMPLICIT_VALUES = 73;
	public static final int TRANSLATE_UNCODED_CHARACTERS = 74;
	public static final int PRINT_ALL_CHARACTERS = 75;
	public static final int LINK_CHARACTERS = 76;
	public static final int EMPHASIZE_FEATURES = 77;
	public static final int REPLACE_ANGLE_BRACKETS = 78;
	public static final int PRINT_UNCODED_CHARACTERS = 79;
	public static final int ITEM_SUBHEADINGS = 80;
	public static final int CHARACTER_HEADINGS = 81;
	public static final int TYPESETTING_MARKS = 82;
	public static final int ALLOWED_VALUES = 83;
	public static final int DEPENDENT_CHARACTERS = 84;
	public static final int LIST_CHARACTERS = 85;
	public static final int LIST_ITEMS = 86;
	public static final int TREAT_INTEGER_AS_REAL = 87;
	public static final int ACCEPT_DUPLICATE_VALUES = 88;
	public static final int OMIT_INAPPLICABLES = 89;
	public static final int INSERT_IMPLICIT_VALUES = 90;
	public static final int PRINT_SUMMARY = 91;
	public static final int DATA_COMPRESSION = 92;
	public static final int INSERT_ITEM_SEQUENCE_NUMBERS = 93;
	public static final int SEQUENCE_INCREMENT = 94;
	public static final int STOP_AFTER_ITEM = 95;
	public static final int INSERT_REDUNDANT_VARIANT_ATTRIBUTES = 96;
	public static final int OMIT_REDUNDANT_VARIANT_ATTRIBUTES = 97;
	public static final int USE_LAST_VALUE_CODED = 98;
	public static final int VOCABULARY = 99;
	public static final int APPLICABLE_CHARACTERS = 100;
	public static final int INAPPLICABLE_CHARACTERS = 101;
	public static final int USE_NORMAL_VALUES = 102;
	public static final int MANDATORY_CHARACTERS = 103;
	public static final int OMIT_OR_FOR_CHARACTERS = 104;
	public static final int NEW_FILES_AT_ITEMS = 105;
	public static final int REPLACE_SEMICOLON_BY_COMMA = 106;
	public static final int REPLACE_STATE_CODES = 107;
	public static final int ADD_CHARACTERS = 108;
	public static final int EMPHASIZE_CHARACTERS = 109;
	public static final int TREAT_VARIABLE_AS_UNKNOWN = 110;
	public static final int SCALE_CHARACTERS = 111;
	public static final int SORT_STATES = 112;
	public static final int CHARACTER_NOTES = 113;
	public static final int FORMATTING_MARKS = 114;
	public static final int USE_MEAN_VALUES = 115;
	public static final int DISABLE_DELTA_OUTPUT = 116;
	public static final int OMIT_SPACE_BEFORE_UNITS = 117;
	public static final int CHARACTER_IMAGES = 118;
	public static final int CHARACTER_FOR_TAXON_IMAGES = 119;
	public static final int OMIT_INNER_COMMENTS = 120;
	public static final int OMIT_COMMENTS = 121;
	public static final int OMIT_LOWER_FOR_CHARACTERS = 122;
	public static final int PERCENT_ERROR = 123;
	public static final int ABSOLUTE_ERROR = 124;
	public static final int OMIT_FINAL_COMMA = 125;
	public static final int ALTERNATE_COMMA = 126;
	public static final int CHINESE_FORMAT = 127;
	public static final int STARTUP_IMAGES = 128;
	public static final int CHARACTER_KEYWORD_IMAGES = 129;
	public static final int TAXON_KEYWORD_IMAGES = 130;
	public static final int CHARACTER_FOR_TAXON_NAMES = 131;
	public static final int CHARACTERS_FOR_SYNONYMY = 132;
	public static final int OUTPUT_FORMAT_HTML = 133;
	public static final int CHARACTER_LIST = 134;
	public static final int ITEM_DESCRIPTIONS = 135;
	public static final int KEY_CHARACTER_LIST = 136;
	public static final int ALTERNATIVE_LANGUAGES = 137;
	public static final int USE_LANGUAGE = 138;
	public static final int ITEM_HEADINGS = 139;
	public static final int CHARACTER_FOR_OUTPUT_FILES = 140;
	public static final int OUTPUT_FORMAT_RTF = 141;
	public static final int INDEX_OUTPUT_FILE = 142;
	public static final int INDEX_TEXT = 143;
	public static final int OUTPUT_DIRECTORY = 144;
	public static final int IMAGE_DIRECTORY = 145;
	public static final int CHECK_FOR_CD = 146;
	public static final int ITEM_OUTPUT_FILES = 147;
	public static final int INDEX_HEADINGS = 148;
	public static final int TRANSLATE_IMPLICIT_VALUES = 149;
	public static final int OVERLAY_FONTS = 150;
	public static final int INSERT_IMAGE_FILE_NAME = 151;
	public static final int USE_CONTROLLING_CHARACTER_FIRST = 152;
	public static final int TAXON_LINKS = 153;
	public static final int NONAUTOMATIC_CONTROLLING_CHARACTERS = 154;
	public static final int SUBJECT_FOR_OUTPUT_FILES = 155;
	public static final int LIST_END = 156; // Insert new directives just BEFORE this!
	
    public static Directive[] ConforDirArray = new Directive[] {
        new Directive(new String[] {"XXX", "XXX", "XXX", "XXX"}, 0, ConforDirType.XXX_XXX_XXX_XXX, DirectiveArgType.DIRARG_OTHER, null, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ABSOLUTE", "ERROR", "", ""}, 4, ConforDirType.ABSOLUTE_ERROR, DirectiveArgType.DIRARG_CHARREALLIST, null, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ACCEPT", "DUPLICATE", "VALUES", ""}, 4, ConforDirType.ACCEPT_DUPLICATE_VALUES, DirectiveArgType.DIRARG_NONE, null, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ADD", "CHARACTERS", "", ""}, 4, ConforDirType.ADD_CHARACTERS, DirectiveArgType.DIRARG_ITEMCHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ALLOWED", "VALUES", "", ""}, 4, ConforDirType.ALLOWED_VALUES, DirectiveArgType.DIRARG_ALLOWED, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ALTERNATE", "COMMA", "", ""}, 4, ConforDirType.ALTERNATE_COMMA, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ALTERNATIVE", "LANGUAGES", "", ""}, 4, ConforDirType.ALTERNATIVE_LANGUAGES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"APPLICABLE", "CHARACTERS", "", ""}, 4, ConforDirType.APPLICABLE_CHARACTERS, DirectiveArgType.DIRARG_INTERNAL, ApplicableCharacters.class, new DirInApplicableChars(), new DirOutApplicableChars()),
        new Directive(new String[] {"CHARACTER", "FOR", "OUTPUT", "FILES"}, 4, ConforDirType.CHARACTER_FOR_OUTPUT_FILES, DirectiveArgType.DIRARG_CHAR, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "FOR", "TAXON", "IMAGES"}, 4, ConforDirType.CHARACTER_FOR_TAXON_IMAGES, DirectiveArgType.DIRARG_CHAR, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "FOR", "TAXON", "NAMES"}, 4, ConforDirType.CHARACTER_FOR_TAXON_NAMES, DirectiveArgType.DIRARG_CHAR, CharacterForTaxonImages.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "HEADINGS", "", ""}, 4, ConforDirType.CHARACTER_HEADINGS, DirectiveArgType.DIRARG_CHARTEXTLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "IMAGES", "", ""}, 4, ConforDirType.CHARACTER_IMAGES, DirectiveArgType.DIRARG_INTERNAL, CharacterImages.class, new DirInCharImages(), new DirOutCharImages()),
        new Directive(new String[] {"CHARACTER", "KEYWORD", "IMAGES", ""}, 4, ConforDirType.CHARACTER_KEYWORD_IMAGES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "LIST", "", ""}, 5, ConforDirType.CHARACTER_LIST, DirectiveArgType.DIRARG_INTERNAL, CharacterList.class, new DirInCharacterList(), new DirOutCharacterList()),
        new Directive(new String[] {"CHARACTER", "NOTES", "", ""}, 4, ConforDirType.CHARACTER_NOTES, DirectiveArgType.DIRARG_INTERNAL, CharacterNotes.class, new DirInCharNotes(), new DirOutCharNotes()),
        new Directive(new String[] {"CHARACTER", "RELIABILITIES", "", ""}, 4, ConforDirType.CHARACTER_RELIABILITIES, DirectiveArgType.DIRARG_CHARREALLIST, CharacterReliabilities.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "TYPES", "", ""}, 2, ConforDirType.CHARACTER_TYPES, DirectiveArgType.DIRARG_INTERNAL, CharacterTypes.class, new DirInCharTypes(), new DirOutCharTypes()),
        new Directive(new String[] {"CHARACTER", "WEIGHTS", "", ""}, 4, ConforDirType.CHARACTER_WEIGHTS, DirectiveArgType.DIRARG_CHARREALLIST, CharacterWeights.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTERS", "FOR", "SYNONYMY", ""}, 4, ConforDirType.CHARACTERS_FOR_SYNONYMY, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHECK", "FOR", "CD", ""}, 0, ConforDirType.CHECK_FOR_CD, DirectiveArgType.DIRARG_TEXT, null, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHINESE", "FORMAT", "", ""}, 4, ConforDirType.CHINESE_FORMAT, DirectiveArgType.DIRARG_NONE, null, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"COMMENT", "", "", ""}, 0, ConforDirType.COMMENT, DirectiveArgType.DIRARG_COMMENT, Comment.class, new DirInComment(), new DirOutDefault()),
        new Directive(new String[] {"DATA", "BUFFER", "SIZE", ""}, 1, ConforDirType.DATA_BUFFER_SIZE, DirectiveArgType.DIRARG_INTEGER, DataBufferSize.class, new DirInDefault(), new DirOutDataBufferSize()),
        new Directive(new String[] {"DATA", "COMPRESSION", "", ""}, 4, ConforDirType.DATA_COMPRESSION, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"DATA", "LISTING", "", ""}, 0, ConforDirType.DATA_LISTING, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"DECIMAL", "PLACES", "", ""}, 4, ConforDirType.DECIMAL_PLACES, DirectiveArgType.DIRARG_CHARINTEGERLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"DEPENDENT", "CHARACTERS", "", ""}, 4, ConforDirType.DEPENDENT_CHARACTERS, DirectiveArgType.DIRARG_INTERNAL, DependentCharacters.class, new DirInDependentChars(), new DirOutDependentChars()),
        new Directive(new String[] {"DISABLE", "DELTA", "OUTPUT", ""}, 4, ConforDirType.DISABLE_DELTA_OUTPUT, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"DIST", "ITEMS", "FILE", ""}, 0, ConforDirType.DIST_ITEMS_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"DIST", "OUTPUT", "FILE", ""}, 0, ConforDirType.DIST_OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EMPHASIZE", "CHARACTERS", "", ""}, 4, ConforDirType.EMPHASIZE_CHARACTERS, DirectiveArgType.DIRARG_ITEMCHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EMPHASIZE", "FEATURES", "", ""}, 4, ConforDirType.EMPHASIZE_FEATURES, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"END", "", "", ""}, 0, ConforDirType.END, DirectiveArgType.DIRARG_NONE, new DirInEnd(), new DirOutDefault()),
        new Directive(new String[] {"ERROR", "FILE", "", ""}, 0, ConforDirType.ERROR_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EXCLUDE", "CHARACTERS", "", ""}, 4, ConforDirType.EXCLUDE_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, ExcludeCharacters.class,  new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EXCLUDE", "ITEMS", "", ""}, 4, ConforDirType.EXCLUDE_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"FORMATTING", "MARKS", "", ""}, 4, ConforDirType.FORMATTING_MARKS, DirectiveArgType.DIRARG_TEXTLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"HEADING", "", "", ""}, 0, ConforDirType.HEADING, DirectiveArgType.DIRARG_TEXT, Heading.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"IMAGE", "DIRECTORY", "", ""}, 0, ConforDirType.IMAGE_DIRECTORY, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"IMPLICIT", "VALUES", "", ""}, 4, ConforDirType.IMPLICIT_VALUES, DirectiveArgType.DIRARG_INTERNAL, ImplicitValues.class, new DirInImplicitValues(), new DirOutImplicitValues()),
        new Directive(new String[] {"INAPPLICABLE", "CHARACTERS", "", ""}, 4, ConforDirType.INAPPLICABLE_CHARACTERS, DirectiveArgType.DIRARG_INTERNAL, InapplicableCharacters.class, new DirInDependentChars(), new DirOutDependentChars()),
        new Directive(new String[] {"INCLUDE", "CHARACTERS", "", ""}, 4, ConforDirType.INCLUDE_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INCLUDE", "ITEMS", "", ""}, 4, ConforDirType.INCLUDE_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INDEX", "OUTPUT", "FILE", ""}, 0, ConforDirType.INDEX_OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INDEX", "TEXT", "", ""}, 0, ConforDirType.INDEX_TEXT, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INDEX", "HEADINGS", "", ""}, 4, ConforDirType.INDEX_HEADINGS, DirectiveArgType.DIRARG_ITEMTEXTLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INPUT", "DELTA", "FILE", ""}, 0, ConforDirType.INPUT_DELTA_FILE, DirectiveArgType.DIRARG_FILE, new DirInInputDeltaFile(), new DirOutDefault()),
        new Directive(new String[] {"INPUT", "FILE", "", ""}, 0, ConforDirType.INPUT_FILE, DirectiveArgType.DIRARG_FILE, InputFile.class, new DirInInputFile(), new DirOutDefault()),
        new Directive(new String[] {"INSERT", "CHARACTER", "SEQUENCE", "NUMBERS"}, 4, ConforDirType.INSERT_CHARACTER_SEQUENCE_NUMBERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INSERT", "IMAGE", "FILE", "NAME"}, 4, ConforDirType.INSERT_IMAGE_FILE_NAME, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INSERT", "IMPLICIT", "VALUES", ""}, 4, ConforDirType.INSERT_IMPLICIT_VALUES, DirectiveArgType.DIRARG_NONE, InsertImplicitValues.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INSERT", "ITEM", "SEQUENCE", "NUMBERS"}, 4, ConforDirType.INSERT_ITEM_SEQUENCE_NUMBERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INSERT", "REDUNDANT", "VARIANT", "ATTRIBUTES"}, 4, ConforDirType.INSERT_REDUNDANT_VARIANT_ATTRIBUTES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INTKEY", "OUTPUT", "FILE", ""}, 0, ConforDirType.INTKEY_OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEM", "ABUNDANCES", "", ""}, 4, ConforDirType.ITEM_ABUNDANCES, DirectiveArgType.DIRARG_ITEMREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEM", "DESCRIPTIONS", "", ""}, 5, ConforDirType.ITEM_DESCRIPTIONS, DirectiveArgType.DIRARG_INTERNAL, ItemDescriptions.class, new DirInItemDescriptions(), new DirOutItemDescriptions()),
        new Directive(new String[] {"ITEM", "HEADINGS", "", ""}, 4, ConforDirType.ITEM_HEADINGS, DirectiveArgType.DIRARG_ITEMTEXTLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEM", "OUTPUT", "FILES", ""}, 4, ConforDirType.ITEM_OUTPUT_FILES, DirectiveArgType.DIRARG_ITEMFILELIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEM", "SUBHEADINGS", "", ""}, 4, ConforDirType.ITEM_SUBHEADINGS, DirectiveArgType.DIRARG_CHARTEXTLIST, ItemSubHeadings.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEM", "WEIGHTS", "", ""}, 4, ConforDirType.ITEM_WEIGHTS, DirectiveArgType.DIRARG_ITEMREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "CHARACTER", "LIST", ""}, 5, ConforDirType.KEY_CHARACTER_LIST, DirectiveArgType.DIRARG_OTHER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "CHARACTERS", "FILE", ""}, 0, ConforDirType.KEY_CHARACTERS_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "ITEMS", "FILE", ""}, 0, ConforDirType.KEY_ITEMS_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "OUTPUT", "FILE", ""}, 0, ConforDirType.KEY_OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"KEY", "STATES", "", ""}, 4, ConforDirType.KEY_STATES, DirectiveArgType.DIRARG_KEYSTATE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LINK", "CHARACTERS", "", ""}, 4, ConforDirType.LINK_CHARACTERS, DirectiveArgType.DIRARG_CHARGROUPS, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LIST", "CHARACTERS", "", ""}, 4, ConforDirType.LIST_CHARACTERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LIST", "HEADING", "", ""}, 0, ConforDirType.LIST_HEADING, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LIST", "ITEMS", "", ""}, 4, ConforDirType.LIST_ITEMS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LISTING", "FILE", "", ""}, 0, ConforDirType.LISTING_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"MANDATORY", "CHARACTERS", "", ""}, 4, ConforDirType.MANDATORY_CHARACTERS, DirectiveArgType.DIRARG_INTERNAL, MandatoryCharacters.class, new DirInMandatoryChars(), new DirOutMandatoryChars()),
        new Directive(new String[] {"MAXIMUM", "NUMBER", "OF", "ITEMS"}, 1, ConforDirType.MAXIMUM_NUMBER_OF_ITEMS, DirectiveArgType.DIRARG_INTERNAL, MaximumNumberOfItems.class, new DirInMaxNumberItems(), new DirOutMaxNumberItems()),
        new Directive(new String[] {"MAXIMUM", "NUMBER", "OF", "STATES"}, 1, ConforDirType.MAXIMUM_NUMBER_OF_STATES, DirectiveArgType.DIRARG_INTERNAL, MaximumNumberOfStates.class, new DirInMaxNumberStates(), new DirOutMaxNumberStates()),
        new Directive(new String[] {"NEW", "FILES", "AT", "ITEMS"}, 4, ConforDirType.NEW_FILES_AT_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NEW", "LISTING", "PAGE", ""}, 0, ConforDirType.NEW_LISTING_PAGE, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NEW", "PARAGRAPHS", "AT", "CHARACTERS"}, 4, ConforDirType.NEW_PARAGRAPHS_AT_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, NewParagraphAtCharacters.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NEW", "PRINT", "PAGE", ""}, 0, ConforDirType.NEW_PRINT_PAGE, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NO", "DATA", "LISTING", ""}, 0, ConforDirType.NO_DATA_LISTING, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NONAUTOMATIC", "CONTROLLING", "CHARACTERS", ""}, 4, ConforDirType.NONAUTOMATIC_CONTROLLING_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NUMBER", "OF", "CHARACTERS", ""}, 1, ConforDirType.NUMBER_OF_CHARACTERS, DirectiveArgType.DIRARG_INTERNAL, NumberOfCharacters.class, new DirInNumberChars(), new DirOutNumberChars()),
        new Directive(new String[] {"NUMBER", "STATES", "FROM", "ZERO"}, 4, ConforDirType.NUMBER_STATES_FROM_ZERO, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NUMBERS", "OF", "STATES", ""}, 3, ConforDirType.NUMBERS_OF_STATES, DirectiveArgType.DIRARG_INTERNAL, NumbersOfStates.class, new DirInNumberStates(), new DirOutNumberStates()),
        new Directive(new String[] {"OMIT", "CHARACTER", "NUMBERS", ""}, 4, ConforDirType.OMIT_CHARACTER_NUMBERS, DirectiveArgType.DIRARG_NONE, OmitCharacterNumbers.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "COMMENTS", "", ""}, 4, ConforDirType.OMIT_COMMENTS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "FINAL", "COMMA", ""}, 4, ConforDirType.OMIT_FINAL_COMMA, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "INAPPLICABLES", "", ""}, 4, ConforDirType.OMIT_INAPPLICABLES, DirectiveArgType.DIRARG_NONE, OmitInapplicables.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "INNER", "COMMENTS", ""}, 4, ConforDirType.OMIT_INNER_COMMENTS, DirectiveArgType.DIRARG_NONE, OmitInnerComments.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "LOWER", "FOR", "CHARACTERS"}, 4, ConforDirType.OMIT_LOWER_FOR_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "OR", "FOR", "CHARACTERS"}, 4, ConforDirType.OMIT_OR_FOR_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "PERIOD", "FOR", "CHARACTERS"}, 4, ConforDirType.OMIT_PERIOD_FOR_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "REDUNDANT", "VARIANT", "ATTRIBUTES"}, 4, ConforDirType.OMIT_REDUNDANT_VARIANT_ATTRIBUTES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "SPACE", "BEFORE", "UNITS"}, 4, ConforDirType.OMIT_SPACE_BEFORE_UNITS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OMIT", "TYPESETTING", "MARKS", ""}, 4, ConforDirType.OMIT_TYPESETTING_MARKS, DirectiveArgType.DIRARG_NONE, OmitTypeSettingMarks.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "DIRECTORY", "", ""}, 0, ConforDirType.OUTPUT_DIRECTORY, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "FILE", "", ""}, 0, ConforDirType.OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "FORMAT", "HTML", ""}, 4, ConforDirType.OUTPUT_FORMAT_HTML, DirectiveArgType.DIRARG_NONE, OutputFormatHtml.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "FORMAT", "RTF", ""}, 4, ConforDirType.OUTPUT_FORMAT_RTF, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "PARAMETERS", "", ""}, 0, ConforDirType.OUTPUT_PARAMETERS, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "WIDTH", "", ""}, 0, ConforDirType.OUTPUT_WIDTH, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OVERLAY", "FONTS", "", ""}, 4, ConforDirType.OVERLAY_FONTS, DirectiveArgType.DIRARG_INTERNAL, OverlayFonts.class, new DirInOverlayFonts(), new DirOutOverlayFonts()),
        new Directive(new String[] {"PAGE", "LENGTH", "", ""}, 0, ConforDirType.PAGE_LENGTH, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PERCENT", "ERROR", "", ""}, 4, ConforDirType.PERCENT_ERROR, DirectiveArgType.DIRARG_CHARREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PREVIOUS", "INPUT", "FILE", ""}, 0, ConforDirType.PREVIOUS_INPUT_FILE, DirectiveArgType.DIRARG_NONE, new DirInPrevInputFile(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "ALL", "CHARACTERS", ""}, 4, ConforDirType.PRINT_ALL_CHARACTERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "APPEND", "", ""}, 0, ConforDirType.PRINT_APPEND, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "CHARACTER", "LIST", ""}, 4, ConforDirType.PRINT_CHARACTER_LIST, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "COMMENT", "", ""}, 0, ConforDirType.PRINT_COMMENT, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "FILE", "", ""}, 0, ConforDirType.PRINT_FILE, DirectiveArgType.DIRARG_FILE, PrintFile.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "HEADING", "", ""}, 0, ConforDirType.PRINT_HEADING, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "ITEM", "DESCRIPTIONS", ""}, 4, ConforDirType.PRINT_ITEM_DESCRIPTIONS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "ITEM", "NAMES", ""}, 4, ConforDirType.PRINT_ITEM_NAMES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "SUMMARY", "", ""}, 4, ConforDirType.PRINT_SUMMARY, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "UNCODED", "CHARACTERS", ""}, 4, ConforDirType.PRINT_UNCODED_CHARACTERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PRINT", "WIDTH", "", ""}, 0, ConforDirType.PRINT_WIDTH, DirectiveArgType.DIRARG_INTEGER, PrintWidth.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REGISTRATION", "HEADING", "", ""}, 0, ConforDirType.REGISTRATION_HEADING, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REGISTRATION", "SUBHEADING", "", ""}, 0, ConforDirType.REGISTRATION_SUBHEADING, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REGISTRATION", "VALIDATION", "", ""}, 0, ConforDirType.REGISTRATION_VALIDATION, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REPLACE", "ANGLE", "BRACKETS", ""}, 4, ConforDirType.REPLACE_ANGLE_BRACKETS, DirectiveArgType.DIRARG_NONE, ReplaceAngleBrackets.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REPLACE", "SEMICOLON", "BY", "COMMA"}, 4, ConforDirType.REPLACE_SEMICOLON_BY_COMMA, DirectiveArgType.DIRARG_CHARGROUPS, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"REPLACE", "STATE", "CODES", ""}, 4, ConforDirType.REPLACE_STATE_CODES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"SCALE", "CHARACTERS", "", ""}, 4, ConforDirType.SCALE_CHARACTERS, DirectiveArgType.DIRARG_CHARREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"SEQUENCE", "INCREMENT", "", ""}, 4, ConforDirType.SEQUENCE_INCREMENT, DirectiveArgType.DIRARG_REAL, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"SHOW", "", "", ""}, 0, ConforDirType.SHOW, DirectiveArgType.DIRARG_TEXT, Show.class, new DirInShow(), new DirOutDefault()),
        new Directive(new String[] {"SORT", "STATES", "", ""}, 4, ConforDirType.SORT_STATES, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"SPECIAL", "STORAGE", "", ""}, 4, ConforDirType.SPECIAL_STORAGE, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"STARTUP", "IMAGES", "", ""}, 4, ConforDirType.STARTUP_IMAGES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"STATE", "CODES", "", ""}, 1, ConforDirType.STATE_CODES, DirectiveArgType.DIRARG_TEXT, new DirInStateCodes(), new DirOutDefault()),
        new Directive(new String[] {"STOP", "AFTER", "ITEM", ""}, 4, ConforDirType.STOP_AFTER_ITEM, DirectiveArgType.DIRARG_ITEM, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"SUBJECT", "FOR", "OUTPUT", "FILES"}, 4, ConforDirType.SUBJECT_FOR_OUTPUT_FILES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TAXON", "IMAGES", "", ""}, 4, ConforDirType.TAXON_IMAGES, DirectiveArgType.DIRARG_INTERNAL, TaxonImages.class, new DirInDefault(), new DirOutTaxonImages()),
        new Directive(new String[] {"TAXON", "KEYWORD", "IMAGES", ""}, 4, ConforDirType.TAXON_KEYWORD_IMAGES, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TAXON", "LINKS", "", ""}, 4, ConforDirType.TAXON_LINKS, DirectiveArgType.DIRARG_ITEMFILELIST, null, null),
        new Directive(new String[] {"TRANSLATE", "IMPLICIT", "VALUES", ""}, 4, ConforDirType.TRANSLATE_IMPLICIT_VALUES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "ALICE", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_ALICE_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "DELTA", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_DELTA_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "DCR", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_DCR_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "DIST", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_DIST_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "HENNIG86", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_HENNIG86_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "INTKEY", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_INTKEY_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "KEY", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_KEY_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "NATURAL", "LANGUAGE"}, 4, ConforDirType.TRANSLATE_INTO_NATURAL_LANGUAGE, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "NEXUS", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_NEXUS_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "PAUP", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_PAUP_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "INTO", "PAYNE", "FORMAT"}, 4, ConforDirType.TRANSLATE_INTO_PAYNE_FORMAT, DirectiveArgType.DIRARG_TRANSLATION, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TRANSLATE", "UNCODED", "CHARACTERS", ""}, 4, ConforDirType.TRANSLATE_UNCODED_CHARACTERS, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TREAT", "INTEGER", "AS", "REAL"}, 4, ConforDirType.TREAT_INTEGER_AS_REAL, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TREAT", "UNKNOWN", "AS", "VARIABLE"}, 4, ConforDirType.TREAT_UNKNOWN_AS_VARIABLE, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TREAT", "VARIABLE", "AS", "UNKNOWN"}, 4, ConforDirType.TREAT_VARIABLE_AS_UNKNOWN, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"TYPESETTING", "MARKS", "", ""}, 4, ConforDirType.TYPESETTING_MARKS, DirectiveArgType.DIRARG_TEXTLIST, TypeSettingMarks.class, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"USE", "CONTROLLING", "CHARACTER", "FIRST"}, 4, ConforDirType.USE_CONTROLLING_CHARACTER_FIRST, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"USE", "LANGUAGE", "", ""}, 4, ConforDirType.USE_LANGUAGE, DirectiveArgType.DIRARG_TEXT, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"USE", "LAST", "VALUE", "CODED"}, 4, ConforDirType.USE_LAST_VALUE_CODED, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"USE", "MEAN", "VALUES", ""}, 4, ConforDirType.USE_MEAN_VALUES, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"USE", "NORMAL", "VALUES", ""}, 4, ConforDirType.USE_NORMAL_VALUES, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"VOCABULARY", "", "", ""}, 4, ConforDirType.VOCABULARY, DirectiveArgType.DIRARG_TEXTLIST, new DirInDefault(), new DirOutDefault())
    };
 
    @SuppressWarnings({ "rawtypes" })
	public static Directive typeOf(AbstractDirective<?> directive) {
		Class<? extends AbstractDirective> directiveClass = directive.getClass();
		for (Directive dir : ConforDirArray) {
			if (directiveClass.equals(dir.getImplementationClass())) {
				return dir;
			}
		}
		return ConforDirArray[0];
	}
    
	static {
		Arrays.sort(ConforDirArray, new Comparator<Directive>() {

			@Override
			public int compare(Directive o1, Directive o2) {
				return o1.getNumber() < o2.getNumber() ? -1 : 1;
			}
		});
	}
	
	
}
