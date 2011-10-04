package au.org.ala.delta.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class FileUtils {

	public static final String BACKUP_FILE_EXTENSION = ".bak";
	
	/**
	 * Checks if a file exists, if so it is backed up using an extension
	 * of ".bak" and the original is deleted.
	 * @param filePath the full path to the file to check and backup.
	 */
	public static void backupAndDelete(String filePath) {
		File directivesFile = new File(filePath);
		if (directivesFile.exists()) {
			File bakFile = new File(directivesFile.getAbsolutePath() + BACKUP_FILE_EXTENSION);
			directivesFile.renameTo(bakFile);
			
			org.apache.commons.io.FileUtils.deleteQuietly(directivesFile);
		}
	}
	
	/**
	 * Checks if a file exists, if so it is backed up using an extension
	 * of ".bak" and the original is deleted.
	 * @param fileName the file name to check (relative).
	 * @param directoryPath the path to the file to check and backup.
	 */
	public static void backupAndDelete(String fileName, String directoryPath) {
		String fullPath = FilenameUtils.concat(directoryPath, fileName);
		backupAndDelete(fullPath);
	}
	
}
