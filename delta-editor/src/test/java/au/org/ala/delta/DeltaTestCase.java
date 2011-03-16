package au.org.ala.delta;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import au.org.ala.delta.editor.slotfile.BinFileMode;
import au.org.ala.delta.editor.slotfile.SlotFile;
import junit.framework.TestCase;

public abstract class DeltaTestCase extends TestCase {
	
	private List<File> _tempFiles = new ArrayList<File>();
	
	/**
	 * Makes a copy of the supplied DELTA file and returns a new SlotFile created from the copied file.
	 * @param fileName the ClassLoader relative name of the DELTA file.
	 * @return a new SlotFile.
	 * @throws IOException if the file cannot be found.
	 */
	protected SlotFile copyAndOpen(String fileName) throws IOException {
		
		URL deltaFileUrl = getClass().getResource(fileName);		
		File tempDeltaFile = File.createTempFile("test", ".dlt");
		
		_tempFiles.add(tempDeltaFile);
		
		FileUtils.copyURLToFile(deltaFileUrl, tempDeltaFile);
		SlotFile slotFile = new SlotFile(tempDeltaFile.getAbsolutePath(), BinFileMode.FM_EXISTING);
		
		return slotFile;
	}

	/**
	 * Copies the specified resource to a temp file, and returns that File
	 * @param fileName the ClassLoader relative name of the file to open.
	 * @return a new File.
	 * @throws IOException if the file cannot be found.
	 */
	protected File copyURLToFile(String filename) throws IOException {
		URL deltaFileUrl = getClass().getResource(filename);		
		File tempFile = File.createTempFile("test", ".dlt");
		_tempFiles.add(tempFile);
		FileUtils.copyURLToFile(deltaFileUrl, tempFile);		
		return tempFile;	
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		for(File f : _tempFiles) {
			System.out.println("Deleting temp file: " + f.getAbsolutePath());
			f.delete();
		}
	}

}
