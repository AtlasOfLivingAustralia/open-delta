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

/**
   Class to hold dictionary keys used by the RTF reader/writer.
   These should be moved into StyleConstants.
*/
class Constants
{
    /** An array of TabStops */
    static final String Tabs = "tabs";

    /** The name of the character set the original RTF file was in */
    static final String RTFCharacterSet = "rtfCharacterSet";

    /** Indicates the domain of a Style */
    static final String StyleType = "style:type";

    /** Value for StyleType indicating a section style */
    static final String STSection = "section";
    /** Value for StyleType indicating a paragraph style */
    static final String STParagraph = "paragraph";
    /** Value for StyleType indicating a character style */
    static final String STCharacter = "character";

    /** The style of the text following this style */
    static final String StyleNext = "style:nextStyle";

    /** Whether the style is additive */
    static final String StyleAdditive = "style:additive";

    /** Whether the style is hidden from the user */
    static final String StyleHidden = "style:hidden";

    /* Miscellaneous character attributes */
    static final String Caps          = "caps";
    static final String Deleted       = "deleted";
    static final String Outline       = "outl";
    static final String SmallCaps     = "scaps";
    static final String Shadow        = "shad";
    static final String Strikethrough = "strike";
    static final String Hidden        = "v";

    /* Miscellaneous document attributes */
    static final String PaperWidth    = "paperw";
    static final String PaperHeight   = "paperh";
    static final String MarginLeft    = "margl";
    static final String MarginRight   = "margr";
    static final String MarginTop     = "margt";
    static final String MarginBottom  = "margb";
    static final String GutterWidth   = "gutter";

    /* This is both a document and a paragraph attribute */
    static final String WidowControl  = "widowctrl";
}
