package au.org.ala.delta.editor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;

/**
 * Preferences facade for the Delta Editor
 */
public class EditorPreferences {

	private static String MRU_PREF_KEY = "MRU";
	
	/** Character used to separate the most recently used filenames */
	private static String MRU_SEPARATOR = "\n";
	/** The maximum number of Most Recently Used files */
	private static int MAX_SIZE_MRU = 4;

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
	 * Adds the supplied filename to the top of the most recently used files.
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

}
