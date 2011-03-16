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

public class DistDirType {
	public static final int XXX_XXX_XXX_XXX = 0;
	public static final int CHARACTER_WEIGHTS = 1;
	public static final int COMMENT = 2;
	public static final int EXCLUDE_CHARACTERS = 3;
	public static final int EXCLUDE_ITEMS = 4;
	public static final int INCLUDE_CHARACTERS = 5;
	public static final int INCLUDE_ITEMS = 6;
	public static final int ITEMS_FILE = 7;
	public static final int LISTING_FILE = 8;
	public static final int LOG = 9;
	public static final int MATCH_OVERLAP = 10;
	public static final int MAXIMUM_ITEMS_IN_MEMORY = 11;
	public static final int MINIMUM_NUMBER_OF_COMPARISONS = 12;
	public static final int NAMES_FILE = 13;
	public static final int OUTPUT_FILE = 14;
	public static final int PHYLIP_FORMAT = 15;
	public static final int LIST_END = 16; // Insert new directives just BEFORE this!
	
    public static Directive[] DistDirArray = new Directive[] {
        new Directive(new String[] {"XXX", "XXX", "XXX", "XXX"}, 0, DistDirType.XXX_XXX_XXX_XXX, DirectiveArgType.DIRARG_OTHER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"CHARACTER", "WEIGHTS", "", ""}, 0, DistDirType.CHARACTER_WEIGHTS, DirectiveArgType.DIRARG_CHARREALLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"COMMENT", "", "", ""}, 0, DistDirType.COMMENT, DirectiveArgType.DIRARG_COMMENT, new DirInComment(), new DirOutDefault()),
        new Directive(new String[] {"EXCLUDE", "CHARACTERS", "", ""}, 0, DistDirType.EXCLUDE_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"EXCLUDE", "ITEMS", "", ""}, 0, DistDirType.EXCLUDE_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INCLUDE", "CHARACTERS", "", ""}, 0, DistDirType.INCLUDE_CHARACTERS, DirectiveArgType.DIRARG_CHARLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"INCLUDE", "ITEMS", "", ""}, 0, DistDirType.INCLUDE_ITEMS, DirectiveArgType.DIRARG_ITEMLIST, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"ITEMS", "FILE", "", ""}, 0, DistDirType.ITEMS_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LISTING", "FILE", "", ""}, 0, DistDirType.LISTING_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"LOG", "", "", ""}, 0, DistDirType.LOG, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"MATCH", "OVERLAP", "", ""}, 0, DistDirType.MATCH_OVERLAP, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"MAXIMUM", "ITEMS", "IN", "MEMORY"}, 0, DistDirType.MAXIMUM_ITEMS_IN_MEMORY, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"MINIMUM", "NUMBER", "OF", "COMPARISONS"}, 0, DistDirType.MINIMUM_NUMBER_OF_COMPARISONS, DirectiveArgType.DIRARG_INTEGER, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"NAMES", "FILE", "", ""}, 0, DistDirType.NAMES_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"OUTPUT", "FILE", "", ""}, 0, DistDirType.OUTPUT_FILE, DirectiveArgType.DIRARG_FILE, new DirInDefault(), new DirOutDefault()),
        new Directive(new String[] {"PHYLIP", "FORMAT", "", ""}, 0, DistDirType.PHYLIP_FORMAT, DirectiveArgType.DIRARG_NONE, new DirInDefault(), new DirOutDefault())
    };
    
	static {
		Arrays.sort(DistDirArray, new Comparator<Directive>() {

			@Override
			public int compare(Directive o1, Directive o2) {
				return o1.getNumber() < o2.getNumber() ? -1 : 1;
			}
		});
	}
    
}
