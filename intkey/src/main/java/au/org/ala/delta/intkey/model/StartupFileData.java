package au.org.ala.delta.intkey.model;

import java.io.File;

public class StartupFileData {

    private String _inkFileLocation;
    private String _dataFileLocation;
    private String _initializationFileLocation;
    private String _imagePath;
    private String infoPath;

    private File _dataFileLocalCopy;
    private File _initializationFileLocalCopy;

    public String getInkFileLocation() {
        return _inkFileLocation;
    }

    public void setInkFileLocation(String inkFileLocation) {
        this._inkFileLocation = inkFileLocation;
    }

    public String getDataFileLocation() {
        return _dataFileLocation;
    }

    public void setDataFileLocation(String dataFileLocation) {
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
        return infoPath;
    }

    public void setInfoPath(String infoPath) {
        this.infoPath = infoPath;
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
}