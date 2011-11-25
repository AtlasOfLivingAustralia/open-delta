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
package au.org.ala.delta.model.attribute;

public class ChunkType {
	  public static final byte CHUNK_STOP = 0;   	// Marker for end of list
	  public static final byte CHUNK_TEXT = 1;       	// Text (a "comment")
	  public static final byte CHUNK_STATE = 2;      	// A state ID
	  public static final byte CHUNK_NUMBER = 3;     	// A "normal" numeric value
	  public static final byte CHUNK_EXLO_NUMBER = 4;	// An extreme low numeric value
	  public static final byte CHUNK_EXHI_NUMBER = 5;	// An extreme high numeric value
	  public static final byte CHUNK_VARIABLE = 6;	   	// The "Variable" pseudo-value
	  public static final byte CHUNK_UNKNOWN = 7;	    // The "Unknown" pseudo-value
	  public static final byte CHUNK_INAPPLICABLE = 8;	// The "Inapplicable" pseudo-value
	  public static final byte CHUNK_OR = 9;				// Connective "or"
	  public static final byte CHUNK_AND = 10;			// Connective "and"
	  public static final byte CHUNK_TO = 11;		        // Connective "to"
	  public static final byte CHUNK_LONGTEXT = 12;   	// Text longer than 0xffff in length.
}
