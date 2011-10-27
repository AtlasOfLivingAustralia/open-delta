package au.org.ala.delta.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.util.FileUtils;

/**
 * The resource settings class is used to configure multiple locations in which
 * dataset resources can be found. Similar in concept to the Java classpath.
 * 
 * @author ChrisF
 * 
 */
public class ResourceSettings {

    public static final String RESOURCE_PATH_SEPARATOR = ";";
    protected List<String> _resourcePaths;
    protected String _dataSetPath;

    public ResourceSettings() {
        super();
    }

    /**
     * Set the dataset path. Any relative resource paths are looked up relative
     * to the dataset path.
     * 
     * @param path
     */
    public void setDataSetPath(String path) {
        _dataSetPath = path;
    }

    /**
     * Get the dataset path. Any relative resource paths are looked up relative
     * to the dataset path.
     * 
     * @return
     */
    public String getDataSetPath() {
        return _dataSetPath;
    }

    /**
     * @return the first entry on the resource path as an absolute file path.
     */
    public String getFirstResourcePath() {
        if (_resourcePaths.isEmpty()) {
            return "";
        } else {
            return getResourcePathLocations().get(0);
        }
    }

    /**
     * @return the list of resource path locations as a ';' separated String.
     */
    public String getResourcePath() {
        if (_resourcePaths.isEmpty()) {
            return "";
        }
        StringBuilder path = new StringBuilder();
        path.append(_resourcePaths.get(0));
        for (int i = 1; i < _resourcePaths.size(); i++) {
            path.append(RESOURCE_PATH_SEPARATOR);
            path.append(_resourcePaths.get(i));
        }
        return path.toString();
    }

    /**
     * @return A list of the resource path locations
     */
    public List<String> getResourcePathLocations() {
        List<String> retList = new ArrayList<String>();

        for (String imagePath : _resourcePaths) {
            if (imagePath.startsWith("http") || new File(imagePath).isAbsolute() || StringUtils.isEmpty(_dataSetPath)) {
                retList.add(imagePath);
            } else {
                retList.add(FilenameUtils.concat(_dataSetPath, imagePath));
            }
        }

        return retList;
    }

    /**
     * Set the list of resource path locations as a ';' separated String.
     * 
     * @param resourcePath
     *            a list of resource path locations as a ';' separated String.
     */
    public void setResourcePath(String resourcePath) {
        _resourcePaths = new ArrayList<String>();

        _resourcePaths.addAll(Arrays.asList(resourcePath.split(RESOURCE_PATH_SEPARATOR)));
    }

    /**
     * Set the list of resource path locations from a list
     * 
     * @param resourcePaths
     */
    public void setResourcePaths(List<String> resourcePaths) {
        _resourcePaths = new ArrayList<String>(resourcePaths);
    }

    /**
     * Checks if a file exists on the resource path.
     * 
     * @param file
     *            the file to check.
     * @return true if the supplied file is on the resource path.
     */
    public boolean isOnResourcePath(File file) {
        String filePath = file.getParent();
        List<String> resourcePaths = getResourcePathLocations();
        for (String path : resourcePaths) {
            if (filePath.equals(FilenameUtils.separatorsToSystem(path))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find a file on the resource path. The individual resource path locations
     * are checked in turn until a file with the specified name is found. The
     * dataset path is also searched if the file cannot be found at any of the
     * resource path locations.
     * 
     * @param fileName
     *            The file name
     * @param ignoreRemoteLocations
     *            if true, any remote resource path locations (i.e. those
     *            specified by http URLS) are ignored when searching.
     * @return A URL for the found file, or null if the file was not found.
     */
    public URL findFileOnResourcePath(String fileName, boolean ignoreRemoteLocations) {
        URL fileLocation = null;

        List<String> locationsToSearch = getResourcePathLocations();

        // If file cannot be found at any of the resource path locations, also
        // search the
        // dataset path itself.
        locationsToSearch.add(getDataSetPath());

        for (String resourcePath : locationsToSearch) {
            try {
                if (resourcePath.toLowerCase().startsWith("http")) {
                    if (ignoreRemoteLocations) {
                        continue;
                    }

                    if (resourcePath.endsWith("/")) {
                        fileLocation = new URL(resourcePath + fileName);
                    } else {
                        fileLocation = new URL(resourcePath + "/" + fileName);
                    }

                    // Try opening a stream to the remote file. If no exceptions
                    // are thrown, the file
                    // was successfully found at that location. Unfortunately
                    // there is no better way to
                    // test existence of a remote file.
                    fileLocation.openStream();
                    break;
                } else {
                    File f = new File(resourcePath + File.separator + fileName);
                    if (f.exists()) {
                        fileLocation = f.toURI().toURL();
                        break;
                    }

                }

            } catch (IOException ioexception) {
                // do nothing, keep searching on image path.
            }
        }

        return fileLocation;
    }

    /**
     * Adds the supplied path to the image path as a path relative to the data
     * set path.
     * 
     * @param selectedFile
     *            a file containing the path to add. If the file is relative it
     *            will be added without modification. Otherwise it will be
     *            turned into a path relative to the data set path and then
     *            added.
     */
    public void addToResourcePath(File selectedFile) {
        String relativePath = FileUtils.makeRelativeTo(_dataSetPath, selectedFile);
        addToResourcePath(relativePath);
    }

    private void addToResourcePath(String relativePath) {
        if (!_resourcePaths.contains(relativePath)) {
            _resourcePaths.add(relativePath);
        }
    }

}