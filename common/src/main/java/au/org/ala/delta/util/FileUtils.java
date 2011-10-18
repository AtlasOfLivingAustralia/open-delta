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
	
	public static String makeRelativeTo(String path, File file) {
		String relativePath;
		if (file.isAbsolute()) {
            File dataSetPath = new File(path);

            File parent = parent(file, dataSetPath);
            File commonParent = dataSetPath;
            String prefix = "";
            while (!parent.equals(commonParent)) {
                prefix += ".." + File.separatorChar;
                commonParent = commonParent.getParentFile();
                parent = parent(file, commonParent);
            }
            String filePath = file.getAbsolutePath();
            String parentPath = parent.getAbsolutePath();

            int relativePathIndex = filePath.indexOf(parentPath) + parentPath.length();
            if (!parentPath.endsWith(File.separator)) {
                relativePathIndex++;
            }
            relativePath = prefix + filePath.substring(relativePathIndex);
        } else {
            relativePath = file.getPath();
        }
		return relativePath;

	}
	
	private static File parent(File start, File parent) {
        if (start.equals(parent) || start.getParentFile() == null) {
            return start;
        } else {
            return parent(start.getParentFile(), parent);
        }
    }
}
