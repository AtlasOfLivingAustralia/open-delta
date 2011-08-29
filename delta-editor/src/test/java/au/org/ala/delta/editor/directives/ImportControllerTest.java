package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Tests the ImportController class. 
 */
public class ImportControllerTest extends AbstractImportControllerTest {

	
	
	protected void createDataSet() throws Exception {
		_dataSet = (SlotFileDataSet)_repository.newDataSet();
	}

	@Test
	public void testSilentImport() throws Exception {
		
		File datasetDirectory = new File(getClass().getResource("/dataset").toURI());
		DirectiveFileInfo specs = new DirectiveFileInfo("specs", DirectiveType.CONFOR);
		DirectiveFileInfo chars = new DirectiveFileInfo("chars", DirectiveType.CONFOR);
		DirectiveFileInfo items = new DirectiveFileInfo("items", DirectiveType.CONFOR);
		
		List<DirectiveFileInfo> files = Arrays.asList(new DirectiveFileInfo[] {specs, chars, items});
		
		importer.new DoImportTask(datasetDirectory, files).doInBackground();
		
		assertEquals(89, _dataSet.getNumberOfCharacters());
		// do a few random assertions
		Character character = _dataSet.getCharacter(10);
		assertEquals(10, character.getCharacterId());
		assertEquals("<adaxial> ligule <presence>", character.getDescription());
		assertEquals(CharacterType.UnorderedMultiState, character.getCharacterType());
		MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
		assertEquals(2, multiStateChar.getNumberOfStates());
		assertEquals("<consistently> present <<implicit>>", multiStateChar.getState(1));
		assertEquals("absent <at least from upper leaves>", multiStateChar.getState(2));
		
		character = _dataSet.getCharacter(48);
		assertEquals("awns <of female-fertile lemmas, if present, number>", character.getDescription());
		assertEquals(CharacterType.IntegerNumeric, character.getCharacterType());
		assertEquals(48, character.getCharacterId());
		
		character = _dataSet.getCharacter(85);
		assertEquals(85, character.getCharacterId());
		assertEquals("<number of species>", character.getDescription());
		assertEquals(CharacterType.IntegerNumeric, character.getCharacterType());
		IntegerCharacter integerCharacter = (IntegerCharacter)character;
		assertEquals("species", integerCharacter.getUnits());
		
		
		assertEquals(14, _dataSet.getMaximumNumberOfItems());
		
		Item item = _dataSet.getItem(5);
		assertEquals(5, item.getItemNumber());
		
		// At the moment getDescription() strips RTF... probably should leave that to the formatter.
		//assertEquals("\\i{}Cynodon\\i0{} <Rich.>", item.getDescription());
		
		assertEquals("\\i{}Cynodon\\i0{} <Rich.>", item.getDescription());
		assertEquals("4-60(-100)", item.getAttribute(_dataSet.getCharacter(2)).getValueAsString());
		assertEquals("3", item.getAttribute(_dataSet.getCharacter(60)).getValueAsString()); 
		
		character = _dataSet.getCharacter(11);
		assertEquals("<adaxial> ligule <form; avoid seedlings>", character.getDescription());
		multiStateChar = (MultiStateCharacter)character;
		assertEquals(4, multiStateChar.getNumberOfStates());
		assertEquals("an unfringed membrane <may be variously hairy or ciliolate>", multiStateChar.getState(1));
		assertEquals("a fringed membrane", multiStateChar.getState(2));
		assertEquals("a fringe of hairs", multiStateChar.getState(3));
		assertEquals("a rim of minute papillae", multiStateChar.getState(4));	
	}
	
	@Test
	public void testToIntImport() throws Exception {
		String toIntPath = "/au/org/ala/delta/editor/directives/expected_results";
		File datasetDirectory = new File(getClass().getResource(toIntPath).toURI());
		DirectiveFileInfo toint = new DirectiveFileInfo("toint", DirectiveType.CONFOR);
		
		List<DirectiveFileInfo> files = Arrays.asList(new DirectiveFileInfo[] {toint});
		
		importer.new DoImportTask(datasetDirectory, files).doInBackground();

		assertEquals(1, _dataSet.getDirectiveFileCount());
		
		DirectiveFile file = _dataSet.getDirectiveFile(1);
		
		assertEquals(24, file.getDirectiveCount());
	}
	
}
