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
package au.org.ala.delta.editor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.ui.EditorAdvanceMode;

/**
 * Preferences facade for the Delta Editor
 */
public class EditorPreferences {

	public static String MRU_PREF_KEY = "MRU";

	/** Character used to separate the most recently used filenames */
	private static String MRU_SEPARATOR = "\n";
	/** The maximum number of Most Recently Used files */
	private static int MAX_SIZE_MRU = 4;

	private static String IMPORT_FILE_FILTER_KEY = "importFileFilter";
	/** Default value for the import file filter */
	private static String DEFAULT_IMPORT_FILE_FILTER = "*.bak;*.box;tidy*.;reorder*.;*.bmp;*.lst;*.prt;*.dlt";

	public static String ADVANCE_MODE_KEY = "EditorAdvanceMode";

	private static EditorAdvanceMode DEFAULT_EDITOR_ADVANCE_MODE = EditorAdvanceMode.Character;
	
	public static String VIEWER_DIVIDER_OFFSET_KEY = "ViewerDividerOffset";
	private static int DEFAULT_VIEWER_DIVIDER_OFFSET = 200;

	/**
	 * @return An array of the most recently used filenames
	 */
	public static String[] getPreviouslyUsedFiles() {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			String mru = prefs.get(MRU_PREF_KEY, "");
			if (!StringUtils.isEmpty(mru)) {
				return mru.split(MRU_SEPARATOR);
			}
		}

		return null;
	}

	/**
	 * Removes the specified file from the most recently used file list
	 * @param filename The filename to remove
	 */
	public static void removeFileFromMRU(String filename) {

		String[] existingFiles = getPreviouslyUsedFiles();

		StringBuilder b = new StringBuilder();
		for (int i = 0; i < existingFiles.length; ++i) {

			if (!existingFiles[i].equalsIgnoreCase(filename)) {

				if (b.length() > 0) {
					b.append(MRU_SEPARATOR);
				}
				b.append(existingFiles[i]);
			}
		}
		
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		prefs.put(MRU_PREF_KEY, b.toString());
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds the supplied filename to the top of the most recently used files.
	 * 
	 * @param filename
	 */
	public static void addFileToMRU(String filename) {

		Queue<String> q = new LinkedList<String>();

		q.add(filename);

		String[] existingFiles = getPreviouslyUsedFiles();
		if (existingFiles != null) {
			for (String existingFile : existingFiles) {
				if (!q.contains(existingFile)) {
					q.add(existingFile);
				}
			}
		}

		StringBuilder b = new StringBuilder();
		for (int i = 0; i < MAX_SIZE_MRU && q.size() > 0; ++i) {
			if (i > 0) {
				b.append(MRU_SEPARATOR);
			}
			b.append(q.poll());
		}

		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		prefs.put(MRU_PREF_KEY, b.toString());
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getImportFileFilter() {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			return prefs.get(IMPORT_FILE_FILTER_KEY, DEFAULT_IMPORT_FILE_FILTER);
		}

		return DEFAULT_IMPORT_FILE_FILTER;
	}

	public static void setImportFileFilter(String filter) {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			prefs.put(IMPORT_FILE_FILTER_KEY, filter);
		}
	}

	public static EditorAdvanceMode getEditorAdvanceMode() {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			String mode = prefs.get(ADVANCE_MODE_KEY, DEFAULT_EDITOR_ADVANCE_MODE.toString());
			return EditorAdvanceMode.valueOf(mode);
		}
		return DEFAULT_EDITOR_ADVANCE_MODE;
	}
	
	public static void setEditorAdvanceMode(EditorAdvanceMode mode) {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			prefs.put(ADVANCE_MODE_KEY, mode.toString());
		}
	}
	
	public static int getViewerDividerOffset() {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			return prefs.getInt(VIEWER_DIVIDER_OFFSET_KEY, DEFAULT_VIEWER_DIVIDER_OFFSET);
			
		}
		return DEFAULT_VIEWER_DIVIDER_OFFSET;		
	}
	
	public static void setViewerDividerOffset(int offset) {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			prefs.putInt(VIEWER_DIVIDER_OFFSET_KEY, offset);
		}
	}

	/**
	 * Allows the supplied PreferenceChangeListener to be notified of changes made to preferences managed by this class.
	 * 
	 * @param listener
	 *            the PreferenceChangeListener to add.
	 */
	public static void addPreferencesChangeListener(PreferenceChangeListener listener) {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			prefs.addPreferenceChangeListener(listener);
		}
	}

	public static void removePreferenceChangeListener(PreferenceChangeListener listener) {
		Preferences prefs = Preferences.userNodeForPackage(DeltaEditor.class);
		if (prefs != null) {
			prefs.removePreferenceChangeListener(listener);
		}

	}

}
