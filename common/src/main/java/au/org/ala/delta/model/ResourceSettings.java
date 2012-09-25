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
package au.org.ala.delta.model;

import au.org.ala.delta.util.FileUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The resource settings class is used to configure multiple locations in which
 * dataset resources can be found. Similar in concept to the Java classpath.
 * 
 * @author ChrisF
 * 
 */
public class ResourceSettings {

    public static final String RESOURCE_PATH_SEPARATOR = ";";
    protected List<String> _resourceLocations;
    protected Set<String> _remoteResourceLocations;
    protected String _dataSetPath;

    /**
     * The directory used to cache files downloaded from remote locations on the
     * resourcePath
     */
    protected File _cacheDir;

    public ResourceSettings() {
        super();
        _resourceLocations = new ArrayList<String>();
        _remoteResourceLocations = new HashSet<String>();
    }

    /**
     * Parses the supplied resource path (essentially a list of ";" separated
     * paths) into a list of Strings.
     * 
     * @param resourcePath
     *            the string to parse.
     * @return a new List<String> containing each path in the supplied
     *         resourcePath.
     */
    public static List<String> parse(String resourcePath) {
        List<String> pathList = new ArrayList<String>();
        if (StringUtils.isEmpty(resourcePath)) {
            return pathList;
        }
        String[] paths = resourcePath.split(RESOURCE_PATH_SEPARATOR);
        for (String path : paths) {
            if (path != null) {
                pathList.add(path.trim());
            }
        }
        return pathList;
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
    public String getFirstResourcePathLocation() {
        if (_resourceLocations.isEmpty()) {
            return "";
        } else {
            return getResourcePathLocations().get(0);
        }
    }

    /**
     * @return the list of resource path locations as a ';' separated String.
     */
    public String getResourcePath() {
        if (_resourceLocations.isEmpty()) {
            return "";
        }
        StringBuilder path = new StringBuilder();
        path.append(_resourceLocations.get(0));
        for (int i = 1; i < _resourceLocations.size(); i++) {
            path.append(RESOURCE_PATH_SEPARATOR);
            path.append(_resourceLocations.get(i));
        }
        return path.toString();
    }

    /**
     * @return A list of the resource path locations
     */
    public List<String> getResourcePathLocations() {
        List<String> retList = new ArrayList<String>();

        for (String resourcePath : _resourceLocations) {
            if (_remoteResourceLocations.contains(resourcePath) || new File(resourcePath).isAbsolute() || StringUtils.isEmpty(_dataSetPath)) {
                retList.add(resourcePath);
            } else {
                retList.add(FilenameUtils.concat(_dataSetPath, resourcePath));
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
        _resourceLocations = ResourceSettings.parse(resourcePath);

        determineRemoteResourceLocations();
    }

    /**
     * Set the list of resource path locations from a list
     * 
     * @param resourcePaths
     */
    public void setResourcePaths(List<String> resourcePaths) {
        _resourceLocations = new ArrayList<String>(resourcePaths);
        determineRemoteResourceLocations();
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

    public URL findFileOnResourcePath(String fileName, boolean ignoreRemoteLocations) {
        return findFileOnResourcePath(fileName, ignoreRemoteLocations, true);
    }

    /**
     * Find a file on the resource path. The individual resource path locations
     * are checked in turn until a file with the specified name is found. The
     * dataset path is also searched if the file cannot be found at any of the
     * resource path locations.
     * 
     * if ignoreCase is true then the exact case is test first, and if the file
     * could not be found a case insensitive search is performed. This is avoid
     * problems with older data sets developed under MS DOS or Windows in which
     * items inconsistently cased names.
     * 
     * If the cacheDir is not null, any files found on the resource path at
     * remote locations will be copied to the cacheDir and the returned URL will
     * point to the cached copy.
     * 
     * if the fileName is already a valid URL string, this URL will be used
     * without the resource path locations being searched. If the URL points to
     * a remote location, the file will still be cached as described above.
     * 
     * @param fileName
     *            The file name
     * @param ignoreRemoteLocations
     *            if true, any remote resource path locations (i.e. those
     *            specified by http URLS) are ignored when searching.
     * @return A URL for the found file. If the found file was at a remote
     *         location, the URL will point to a local cached copy of the file,
     *         or null if the file was not found.
     */
    public URL findFileOnResourcePath(String fileName, boolean ignoreRemoteLocations, boolean ignoreCase) {
        // If the fileName is a valid URL in its own right, simply return it as
        // a URL object - don't search the resource path locations

        URL fileLocation = null;

        // Check if fileName is a valid URL in itself. If it is, don't bother
        // searching the resource path.
        try {
            fileLocation = new URL(fileName);
            try {
                URL urlForCachedFile = cacheFileIfRemote(fileName, fileLocation);
                return urlForCachedFile;
            } catch (IOException ex) {
                // cache operation failed. return location to the original file.
                return fileLocation;
            }
        } catch (MalformedURLException ex) {
            // do nothing
        }

        // Check to see if the filename is an absolute filename on the
        // file system
        File file = new File(fileName);
        if (file.exists() && file.isAbsolute()) {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException ex) {
                // Ignore
            }
        }

        if (ignoreCase) {
            file = FileUtils.findFileIgnoreCase(file);
            if (file != null) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ex) {
                    // Ignore
                }
            }
        }

        // Finally, before searching the resource path, check if the file has previously been saved in the cache
        URL urlForPreviouslyCachedFile = loadFileFromCache(fileName);
        if (urlForPreviouslyCachedFile != null) {
            return urlForPreviouslyCachedFile;
        }

        List<String> locationsToSearch = getResourcePathLocations();
        // If file cannot be found at any of the resource path locations, also
        // search the
        // dataset path itself.
        locationsToSearch.add(getDataSetPath());

        for (String resourcePath : locationsToSearch) {
            try {
                if (_remoteResourceLocations.contains(resourcePath)) {
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
                    InputStream stream = fileLocation.openStream();
                    stream.close();
                    break;
                } else {
                    File f = new File(resourcePath + File.separator + fileName);
                    if (f.exists()) {
                        fileLocation = f.toURI().toURL();
                        break;
                    } else {
                        if (ignoreCase) {
                            f = FileUtils.findFileIgnoreCase(f);
                            if (f != null) {
                                fileLocation = f.toURI().toURL();
                                break;
                            }
                        }
                    }

                }

            } catch (IOException ioexception) {
                // do nothing, keep searching on image path.
            }
        }

        if (fileLocation == null) {
            return fileLocation;
        } else {
            try {
                // Attempt to cache the file found on the resource path.
                URL urlForCachedFile = cacheFileIfRemote(fileName, fileLocation);
                return urlForCachedFile;
            } catch (IOException ex) {
                // cache operation failed. return location to the original file.
                return fileLocation;
            }
        }
    }

    // Use the md5 hash of the URL as the name for the copy of the file
    // in the cache directory. Include the file extension as this is needed in
    // some cases
    // to work out how to open the file.
    private String generateCacheNameForFile(String fileName) {
        String fileExtension = FilenameUtils.getExtension(fileName);
        String md5Hash = DigestUtils.md5Hex(fileName);
        String cacheFileName = md5Hash + "." + fileExtension;
        return cacheFileName;
    }

    private URL loadFileFromCache(String fileName) {
        String cacheFileName = generateCacheNameForFile(fileName);
        File cachedFile = new File(_cacheDir, cacheFileName);
        if (cachedFile.exists()) {
            try {
                return cachedFile.toURI().toURL();
            } catch (MalformedURLException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 
     * Cache the supplied file if it is not stored locally
     *  
     * @param fileName - the name of the file
     * @param fileLocation - the URL of at which the file is located
     * @return A URL. If the original file is stored remotely, this will be a URL pointing to a cached local copy of the file.
     * If the original file is stored locally, the URL will be same as the supplied fileLocation
     * @throws IOException
     */
    private URL cacheFileIfRemote(String fileName, URL fileLocation) throws IOException {
        // Don't bother caching if the URL points to a local file.
        if (!fileLocation.getProtocol().equalsIgnoreCase("file")) {
            String cacheFileName = generateCacheNameForFile(fileName);
            File cachedCopy = new File(_cacheDir, cacheFileName);
            if (!cachedCopy.exists()) {
                // download the file and save it to the cache directory
                org.apache.commons.io.FileUtils.copyURLToFile(fileLocation, cachedCopy);
                cachedCopy.deleteOnExit();
                
                return cachedCopy.toURI().toURL();
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
        if (selectedFile != null) {
            // Try to make into a relative path
            String relativePath = FileUtils.makeRelativeTo(_dataSetPath, selectedFile);
            if (relativePath != null) {
                addToResourcePath(relativePath);
            } else {
                // This path cannot be made into a relative path, so add the
                // absolute path
                addToResourcePath(selectedFile.getAbsolutePath());
            }
        }
    }

    private void addToResourcePath(String relativePath) {
        if (!_resourceLocations.contains(relativePath)) {
            _resourceLocations.add(relativePath);
        }
    }

    /**
     * Go through the list of resource locations and determine which ones are
     * remote. I.e. which do not point to locations on local disks.
     */
    private void determineRemoteResourceLocations() {
        for (String location : _resourceLocations) {
            if (isResourceLocationRemote(location)) {
                _remoteResourceLocations.add(location);
            }
        }
    }

    private boolean isResourceLocationRemote(String location) {
        try {
            // If the location string can be parsed as a URL, then it is deemed
            // to be a remote location
            new URL(location);
            return true;
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    public void setCacheDirectory(File cacheDir) {
        _cacheDir = cacheDir;
    }

}
