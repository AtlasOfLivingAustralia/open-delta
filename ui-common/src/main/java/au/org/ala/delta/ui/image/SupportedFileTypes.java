package au.org.ala.delta.ui.image;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Helper for image and sound file formats supported by the DELTA software.
 */
public class SupportedFileTypes {

	public static FileFilter getSupportedImageFilesFilter() {
		return new FileNameExtensionFilter("Image files", "gif", "jpeg", "png", "bmp");
	}
	
	public static FileFilter getSupportedSoundFilesFilter() {
		return new FileNameExtensionFilter("Sound files", "wav", "mp3");
	}
}
