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
package au.org.ala.delta.intkey.model;

/**
 * Type of display images report. For use with DISPLAY IMAGES directive
 * 
 * @author ChrisF
 * 
 */
public enum DisplayImagesReportType {
    /**
     * List of missing images
     */
    MISSING_IMAGE_LIST,

    /**
     * List of character images
     */
    CHARACTER_IMAGE_LIST,

    /**
     * List of taxon images
     */
    TAXON_IMAGE_LIST
}
