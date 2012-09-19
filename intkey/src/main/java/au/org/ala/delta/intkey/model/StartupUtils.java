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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


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
        StartupFileData startupFileData = context.getStartupFileData();
        File datasetZip = startupFileData.getDataFileLocalCopy();
        
        //Copy the zipped dataset as downloaded from the web
        FileUtils.copyFileToDirectory(datasetZip, saveDir);

        File copyZipFile = new File(saveDir, datasetZip.getName());
        
        //Write a new .ink file 
        File newInkFile = new File(saveDir, FilenameUtils.getName(startupFileData.getInkFileLocation().getFile()));
        
        FileWriter fw = new FileWriter(newInkFile);
        BufferedWriter bufFW = new BufferedWriter(fw);
        
        bufFW.append(INIT_FILE_INK_FILE_KEYWORD);
        bufFW.append("=");
        bufFW.append(newInkFile.toURI().toURL().toString());
        bufFW.append("\n");
        
        bufFW.append(INIT_FILE_DATA_FILE_KEYWORD);
        bufFW.append("=");
        bufFW.append(copyZipFile.toURI().toURL().toString());
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
