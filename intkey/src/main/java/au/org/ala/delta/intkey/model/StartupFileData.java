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

import java.io.File;
import java.net.URL;

public class StartupFileData {

    private URL _inkFileLocation;
    private URL _dataFileLocation;
    private String _initializationFileLocation;
    private String _imagePath;
    private String _infoPath;
    private boolean _remoteDataset;

    private File _dataFileLocalCopy;
    private File _initializationFileLocalCopy;

    public URL getInkFileLocation() {
        return _inkFileLocation;
    }

    public void setInkFileLocation(URL inkFileLocation) {
        this._inkFileLocation = inkFileLocation;
    }

    public URL getDataFileLocation() {
        return _dataFileLocation;
    }

    public void setDataFileLocation(URL dataFileLocation) {
        this._dataFileLocation = dataFileLocation;
    }

    public String getInitializationFileLocation() {
        return _initializationFileLocation;
    }

    public void setInitializationFileLocation(String initializationFileLocation) {
        this._initializationFileLocation = initializationFileLocation;
    }

    public String getImagePath() {
        return _imagePath;
    }

    public void setImagePath(String imagePath) {
        this._imagePath = imagePath;
    }

    public String getInfoPath() {
        return _infoPath;
    }

    public void setInfoPath(String infoPath) {
        this._infoPath = infoPath;
    }

    public File getDataFileLocalCopy() {
        return _dataFileLocalCopy;
    }

    public void setDataFileLocalCopy(File dataFileLocalCopy) {
        this._dataFileLocalCopy = dataFileLocalCopy;
    }

    public File getInitializationFileLocalCopy() {
        return _initializationFileLocalCopy;
    }

    public void setInitializationFileLocalCopy(File initializationFileLocalCopy) {
        this._initializationFileLocalCopy = initializationFileLocalCopy;
    }
    
    public boolean isRemoteDataset() {
        return _remoteDataset;
    }

    public void setRemoteDataset(boolean remoteDataset) {
        this._remoteDataset = remoteDataset;
    }
}
