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
 * Various constant values used by intkey
 * 
 * @author ChrisF
 * 
 */
public class Constants {

    public static final int RECORD_LENGTH_INTEGERS = 32;
    public static final int RECORD_LENGTH_BYTES = 128;

    public static final int SIZE_INT_IN_BYTES = Integer.SIZE / Byte.SIZE;
    public static final int SIZE_FLOAT_IN_BYTES = Float.SIZE / Byte.SIZE;

    public static final int DATASET_MAJOR_VERSION = 5;
    public static final int DATASET_MINOR_VERSION = 2;

    /**
     * Headers names in JNLP style "webstart" dataset files
     */
    public static final String INIT_FILE_INK_FILE_KEYWORD = "InkFile";
    public static final String INIT_FILE_DATA_FILE_KEYWORD = "DataFile";
    public static final String INIT_FILE_INITIALIZATION_FILE_KEYWORD = "InitializationFile";
    public static final String INIT_FILE_IMAGE_PATH_KEYWORD = "ImagePath";
    public static final String INIT_FILE_INFO_PATH_KEYWORD = "InfoPath";

}
