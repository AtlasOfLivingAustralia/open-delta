package au.org.ala.delta.intkey.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


public class StartupUtils {
    
    public static final String INIT_FILE_INK_FILE_KEYWORD = "InkFile";
    public static final String INIT_FILE_DATA_FILE_KEYWORD= "DataFile";
    public static final String INIT_FILE_INITIALIZATION_FILE_KEYWORD = "InitializationFile";
    public static final String INIT_FILE_IMAGE_PATH_KEYWORD = "ImagePath";
    public static final String INIT_FILE_INFO_PATH_KEYWORD = "InfoPath";

    public static void loadDataset(IntkeyContext context, File startupFile) {
        //TODO
    }
    
    public static File saveRemoteDataset(IntkeyContext context, File saveDir) throws IOException {
        File startupFile = context.getDatasetStartupFile();
        StartupFileData startupFileData = context.getStartupFileData();
        File datasetZip = startupFileData.getDataFileLocalCopy();
        
        //Copy the zipped dataset as downloaded from the web
        FileUtils.copyFileToDirectory(datasetZip, saveDir);
        
        //Write a new .ink file 
        File newInkFile = new File(saveDir, startupFile.getName());
        
        FileWriter fw = new FileWriter(newInkFile);
        BufferedWriter bufFW = new BufferedWriter(fw);
        
        bufFW.append(INIT_FILE_INK_FILE_KEYWORD);
        bufFW.append("=");
        bufFW.append(newInkFile.toURI().toURL().toString());
        bufFW.append("\n");
        
        bufFW.append(INIT_FILE_DATA_FILE_KEYWORD);
        bufFW.append("=");
        bufFW.append(startupFileData.getDataFileLocation().toString());
        bufFW.append("\n");
        
        bufFW.append(INIT_FILE_INITIALIZATION_FILE_KEYWORD);
        bufFW.append("=");
        bufFW.append(startupFileData.getInitializationFileLocation());
        bufFW.append("\n");     
        
        String imagePath = startupFileData.getImagePath();
        if (imagePath != null) {
            bufFW.append(INIT_FILE_IMAGE_PATH_KEYWORD);
            bufFW.append("=");
            bufFW.append(imagePath);
            bufFW.append("\n");
        }
        
        String infoPath = startupFileData.getInfoPath();
        if (infoPath != null) {
            bufFW.append(INIT_FILE_INFO_PATH_KEYWORD);
            bufFW.append("=");
            bufFW.append(infoPath);
            bufFW.append("\n");
        }
        
        bufFW.flush();
        bufFW.close();
        
        return newInkFile;
    }

}
