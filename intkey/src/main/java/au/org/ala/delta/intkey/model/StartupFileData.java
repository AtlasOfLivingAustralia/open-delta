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

/**
 * In memory representation of the data contained in the JNLP-style startup
 * files for delta datasets.
 * 
 * @author ChrisF
 * 
 */
public class StartupFileData {

    /**
     * Remote location at which the startup file itself can be located
     */
    private URL _inkFileLocation;

    /**
     * Remote location for the zip file containing the intkey dataset
     */
    private URL _dataFileLocation;

    /**
     * The name of the directives file inside the dataset zip archive to use to
     * initialize the dataset
     */
    private String _initializationFileLocation;

    /**
     * A semi-colon separated list of locations to search for dataset images.
     * These may be relative locations inside the unzipped dataset, or remote
     * locations
     */
    private String _imagePath;

    /**
     * A semi-colon separated list of locations to search for dataset
     * information files. These may be relative locations inside the unzipped
     * dataset, or remote locations
     */
    private String _infoPath;

    /**
     * If true, the dataset was downloaded from a remote location. If false, the
     * dataset is one that has been saved locally.
     */
    private boolean _remoteDataset;

    /**
     * Local copy of the zip file containing the dataset
     */
    private File _dataFileLocalCopy;

    /**
     * Location on local disk for the dataset initialization file, extracted
     * from the zip file.
     */
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
