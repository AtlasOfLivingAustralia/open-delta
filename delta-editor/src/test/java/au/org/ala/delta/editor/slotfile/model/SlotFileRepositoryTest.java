package au.org.ala.delta.editor.slotfile.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOP;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.DeltaDataSet;

/**
 * Tests the SlotFileRepository class.
 */
public class SlotFileRepositoryTest {

	/** The instance of the class we are testing */
	private SlotFileRepository _slotFileRepository;
	
	
	@Before public void setUp() {
		_slotFileRepository = new SlotFileRepository();
	}
	
	
	/**
	 * Makes a copy of the supplied DELTA file and returns a new File created from the copied file.
	 * @param fileName the ClassLoader relative name of the DELTA file.
	 * @return a new File.
	 * @throws IOException if the file cannot be found.
	 */
	private File copyToTemp(String fileName) throws IOException {
		
		URL deltaFileUrl = getClass().getResource(fileName);		
		File tempDeltaFile = File.createTempFile("SlotFileRepositoryTest", ".dlt");
		
		
		FileUtils.copyURLToFile(deltaFileUrl, tempDeltaFile);
		tempDeltaFile.deleteOnExit();
		
		return tempDeltaFile;
	}
	
	
	/**
	 * Tests that the saveAs operation makes an equivalent copy of a file.
	 * Note that the file may not be exactly the same as the save as operation
	 * allows the file to be reordered and deleted slots removed.
	 * @throws Exception if something goes wrong during the test.
	 */
	@Test public void testSaveAsName() throws Exception {
		
		File testFile = null;
		File copy = null;
		DeltaVOP originalVOP = null;
		DeltaVOP copiedVOP = null;
		try {
			testFile = copyToTemp("/SAMPLE.DLT");
			
			DeltaDataSet data = _slotFileRepository.findByName(testFile.getAbsolutePath(), null);
		
			copy = File.createTempFile("SlotFileRepositoryTest", ".dlt");
			System.out.println(copy.getAbsolutePath());
			
			_slotFileRepository.saveAsName(data, copy.getAbsolutePath(), null);
			System.out.println(testFile.getAbsolutePath());
			
			DeltaDataSet copySet = _slotFileRepository.findByName(copy.getAbsolutePath(), null);
			
			originalVOP = getVOP(data);
			copiedVOP = getVOP(copySet);
			
			assertEquals(originalVOP.getDeltaMaster().getNChars(), copiedVOP.getDeltaMaster().getNChars());
			assertEquals(originalVOP.getDeltaMaster().getNItems(), copiedVOP.getDeltaMaster().getNItems());
			
		}
		finally {
			delete(copiedVOP, copy);
			delete(originalVOP, testFile);
		}
	}
	
	private DeltaVOP getVOP(DeltaDataSet dataSet) {
		return ((SlotFileDataSet)dataSet).getVOP();
	}
	
	private void delete(VOP vop, File file) {
		try {
			if (vop != null) {
				vop.close();
			}
			if (file != null) {
				file.delete();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
