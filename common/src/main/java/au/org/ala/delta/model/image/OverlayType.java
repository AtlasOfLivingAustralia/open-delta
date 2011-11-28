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
package au.org.ala.delta.model.image;

public class OverlayType {
	// NOTE! Changes here must be made to both array OLKeywords and enum OverlayType
	// They MUST have entries "in parallel".
	// The negative values are used for "unnamed" overlay types, that are used
	// internally for editing.
	public static final int OLBUTTONBLOCK = -3; // Used only when modifying aligned push-buttons
	public static final int OLHOTSPOT = -2; // Not a "real" overlay type; used for convenience in editing
	public static final int OLNONE = -1; // Undefined; the remaining values MUST correspond
											// with array OLKeywords.
	public static final int OLTEXT = 0; // Use a literal text string
	public static final int OLITEM = 1; // Use name of the item
	public static final int OLFEATURE = 2; // Use name of the character
	public static final int OLSTATE = 3; // Use name of the state (selectable)
	public static final int OLVALUE = 4; // Use specified values or ranges (selectable)
	public static final int OLUNITS = 5; // Use units (for numeric characters)
	public static final int OLENTER = 6; // Create edit box for data entry
	public static final int OLSUBJECT = 7; // Has text for menu entry
	public static final int OLSOUND = 8; // Has name of .WAV sound file
	public static final int OLHEADING = 9; // Using heading string for the data-set
	public static final int OLKEYWORD = 10; // Use specified keyword(s)
	public static final int OLOK = 11; // Create OK pushbutton
	public static final int OLCANCEL = 12; // Create Cancel pushbutton
	public static final int OLNOTES = 13; // Create Notes pushbutton (for character notes)
	public static final int OLIMAGENOTES = 14; // Create Notes pushbutton (for notes about the image)
	public static final int OLCOMMENT = 15; // Not a "real" overlay type, but used to save comments addressed
	// to images rather than overlays
	public static final int LIST_END = 16; // Insert new overlay types just BEFORE this!

	public static String[] OLKeywords = new String[] { "text", "item", "feature", "state", "value", "units", "enter", "subject", "sound", "heading", "keyword", "ok", "cancel", "notes", "imagenotes",
	"comment" };
	
	public static boolean isTextOverlay(ImageOverlay overlay) {
		int overlayType = overlay.type;
		switch (overlayType) {
		case OLTEXT:
		case OLITEM:
		case OLFEATURE:
		case OLSTATE:
		case OLVALUE:
		case OLUNITS:
		case OLENTER:
		case OLKEYWORD:
		case OLOK:
		case OLCANCEL:
		case OLNOTES:
		case OLIMAGENOTES:
		case OLCOMMENT :
			return true;
		default:
			return false;
		}
	}
	
	public static String keywordFromType(int overlayType) {
		return OLKeywords[overlayType];
	}
	
	public static int typeFromKeyword(String keyword) {
		for (int i=0; i<OLKeywords.length; i++) {
			if (OLKeywords[i].equals(keyword)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Invalid keyword : "+keyword);
	}
	
}
