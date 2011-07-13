package au.org.ala.delta.editor.directives;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

/**
 * Tests the ImportController class.  The SuppressWarnings annotation is to prevent warnings
 * about accessing the AppContext which is required to do the thread synchronization 
 * necessary to make the tests run in a repeatable manner.
 */
public class ImportControllerTest extends TestCase {

	/**
	 * Allows us to manually set the data set to be returned from the
	 * getCurrentDataSet method.
	 */
	private class DeltaEditorTestHelper extends DeltaEditor {
		private EditorDataModel _model;
		public void setModel(EditorDataModel model) {
			_model = model;
		}
		
		@Override
		public EditorDataModel getCurrentDataSet() {
			return _model;
		}
		
		
	}
	
	public DeltaEditorTestHelper createTestHelper() throws Exception {
		
		DeltaEditorTestHelper helper = new DeltaEditorTestHelper();
		ApplicationContext context = helper.getContext();
		context.setApplicationClass(DeltaEditor.class);
		Method method = ApplicationContext.class.getDeclaredMethod("setApplication", Application.class);
		method.setAccessible(true);
		method.invoke(context, helper);
	
		return helper;
	}
	
	
	/** The instance of the class we are testing */
	private ImportController importer;
	
	/** The data set we are importing into */
	private SlotFileDataSet _dataSet;
	
	
	@Before
	public void setUp() throws Exception {
		// Sure hope this won't throw a headless exception at some point...
		DeltaEditorTestHelper helper = createTestHelper();
		_dataSet = (SlotFileDataSet)new SlotFileDataSetFactory().createDataSet("test");
		EditorDataModel model = new EditorDataModel(_dataSet);
		helper.setModel(model);

		importer = new ImportController(helper);
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
		UnorderedMultiStateCharacter multiStateChar = (UnorderedMultiStateCharacter)character;
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
		
	}
	
	@Test
	public void testToIntImport() throws Exception {
		String toIntPath = "/au/org/ala/delta/editor/directives/expected_results";
		File datasetDirectory = new File(getClass().getResource(toIntPath).toURI());
		DirectiveFileInfo toint = new DirectiveFileInfo("toint", DirectiveType.CONFOR);
		
		List<DirectiveFileInfo> files = Arrays.asList(new DirectiveFileInfo[] {toint});
		
		importer.new DoImportTask(datasetDirectory, files).doInBackground();

		assertEquals(1, _dataSet.getDirectiveFileCount());
		
		//DirectiveFile file = _dataSet.getDirectiveFile(1);
		
		//assertEquals(24, file.getDirectiveCount());
	}
	
}
