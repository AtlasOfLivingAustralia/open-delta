package au.org.ala.delta;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import au.org.ala.delta.editor.slotfile.SlotFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.rtf.RTFUtils;
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
	
	
	protected File newTempFile() throws IOException {
		File tempFile = File.createTempFile("test", ".dlt");
		_tempFiles.add(tempFile);
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

	/**
	 * Reads all of the Characters and Items from a data set.  Makes a good double check
	 * for tests that appear to work yet have the side effect of corrupting the slot file 
	 * in some way.
	 * @param ds the data set to read.
	 */
	protected void deepRead(DeltaDataSet ds) {
		// Chars...
		System.out.println("Processing " + ds.getNumberOfCharacters() + " characters");
		for (int i = 1; i <= ds.getNumberOfCharacters(); ++i) {
			System.out.println("Processing character: "+i);
			au.org.ala.delta.model.Character ch = ds.getCharacter(i);
	
			ch.getDescription();
			ch.getNotes();
			ch.isExclusive();
			ch.isMandatory();
	
			switch (ch.getCharacterType()) {
			case UnorderedMultiState:
			case OrderedMultiState:
				MultiStateCharacter msc = (MultiStateCharacter) ch;
				msc.getStates();
				break;
			default:
	
			}
		}
	
		System.out.println("Processing " + ds.getMaximumNumberOfItems() + " Items");
		for (int i = 1; i <= ds.getMaximumNumberOfItems(); ++i) {
			Item item = ds.getItem(i);
	
			for (int j = 1; j <= ds.getNumberOfCharacters(); ++j) {
				au.org.ala.delta.model.Character ch = ds.getCharacter(j);
				au.org.ala.delta.model.Attribute a = item.getAttribute(ch);
				if (a != null) {
				
					String strValue = a.getValueAsString();
					if (ch.getCharacterType() == CharacterType.Text) {
						RTFUtils.stripFormatting(strValue);
					}
				}
			}
		}
	
	}

}
