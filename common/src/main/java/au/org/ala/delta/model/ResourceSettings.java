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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	protected List<String> _resourceLocations;
	protected Set<String> _remoteResourceLocations;
	protected String _dataSetPath;

	public ResourceSettings() {
		super();
        _resourceLocations = new ArrayList<String>();
		_remoteResourceLocations = new HashSet<String>();
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
			if (_remoteResourceLocations.contains(resourcePath)
					|| new File(resourcePath).isAbsolute()
					|| StringUtils.isEmpty(_dataSetPath)) {
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
		_resourceLocations = new ArrayList<String>();
		_resourceLocations.addAll(Arrays.asList(resourcePath
				.split(RESOURCE_PATH_SEPARATOR)));
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

	public URL findFileOnResourcePath(String fileName,
			boolean ignoreRemoteLocations) {
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
	 * if the fileName is already a valid URL string, a URL object will be
	 * created an returned, without the resource path locations being searched.
	 * 
	 * @param fileName
	 *            The file name
	 * @param ignoreRemoteLocations
	 *            if true, any remote resource path locations (i.e. those
	 *            specified by http URLS) are ignored when searching.
	 * @return A URL for the found file, or null if the file was not found.
	 */
	public URL findFileOnResourcePath(String fileName,
			boolean ignoreRemoteLocations, boolean ignoreCase) {
		// If the fileName is a valid URL in its own right, simply return it as
		// a URL object - don't search the resource path locations

		URL fileLocation = null;

		try {
			fileLocation = new URL(fileName);
			return fileLocation;
		} catch (MalformedURLException ex) {
			// do nothing
		}

		List<String> locationsToSearch = getResourcePathLocations();

		// First check to see if the filename is an absolute filename on the
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
					fileLocation.openStream();
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
				// This path cannot be made into a relative path, so add the absolute path
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

}
