package au.org.ala.delta.intkey.model;

public class Constants {

    public static final int RECORD_LENGTH_INTEGERS = 32;
    public static final int RECORD_LENGTH_BYTES = 128;
    
    public static final int SIZE_INT_IN_BYTES = Integer.SIZE / Byte.SIZE;
    public static final int SIZE_FLOAT_IN_BYTES = Float.SIZE / Byte.SIZE;
    
    public static final int DATASET_MAJOR_VERSION = 5;
    public static final int DATASET_MINOR_VERSION = 2;
    
    public static final String INIT_FILE_INK_FILE_KEYWORD = "InkFile";
    public static final String INIT_FILE_DATA_FILE_KEYWORD= "DataFile";
    public static final String INIT_FILE_INITIALIZATION_FILE_KEYWORD = "InitializationFile";
    public static final String INIT_FILE_IMAGE_PATH_KEYWORD = "ImagePath";
    public static final String INIT_FILE_INFO_PATH_KEYWORD = "InfoPath";
    
}
