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