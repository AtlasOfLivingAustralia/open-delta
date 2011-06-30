package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import org.junit.Before;

import sun.awt.AppContext;
import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.model.AbstractObservableDataSet;

/**
 * Tests the ExportController class.  The SuppressWarnings annotation is to prevent warnings
 * about accessing the AppContext which is required to do the thread synchronization 
 * necessary to make the tests run in a repeatable manner.
 */
@SuppressWarnings("restriction")
public class ExportControllerTest extends DeltaTestCase {

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
	
	
	/** The instance of the class we are testing */
	private ExportController exporter;
	
	/** The data set we are exporting */
	private SlotFileDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		// Sure hope this won't throw a headless exception at some point...
		DeltaEditorTestHelper helper = new DeltaEditorTestHelper();
	
		File f = copyURLToFile("/SAMPLE.DLT");
			
		DeltaVOP vop = new DeltaVOP(f.getAbsolutePath(), false);
		
		SlotFileDataSetFactory factory = new SlotFileDataSetFactory(vop);
		
		_dataSet = (SlotFileDataSet)factory.createDataSet("test");
		EditorDataModel model = new EditorDataModel(_dataSet);
		helper.setModel(model);
		
		exporter = new ExportController(helper);
	}
	
	public void testSilentExport() throws Exception {
		
		
		DirectiveFileInfo specs = new DirectiveFileInfo("specs", DirectiveType.CONFOR);
		DirectiveFileInfo chars = new DirectiveFileInfo("chars", DirectiveType.CONFOR);
		DirectiveFileInfo items = new DirectiveFileInfo("items", DirectiveType.CONFOR);
		
		DirectiveFile directiveFile = _dataSet.getDirectiveFile(1);
		DirectiveFileInfo test = new DirectiveFileInfo(directiveFile.getFileName(), DirectiveType.CONFOR, directiveFile);
		
		List<DirectiveFileInfo> files = Arrays.asList(new DirectiveFileInfo[] {test});
		File tempDir = new File("/tmp");
		//exporter.doSilentExport(tempDir, files);
		
		
		// Because the import happens on a background (daemon) thread, we have to wait until 
		// the import is finished before doing our assertions.
		//waitForTaskCompletion();
		
		/*
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
		*/
	}
	
	/**
	 * This is a way we can wait for the import task to complete without adding extra methods
	 * to the ImportController just for the unit test.
	 */
	private void waitForTaskCompletion() throws Exception {
		final AppContext appContext = AppContext.getAppContext();
	     ExecutorService executorService =
	            (ExecutorService) appContext.get(SwingWorker.class);
	     executorService.shutdown();
	     executorService.awaitTermination(10, TimeUnit.SECONDS);
	}
	
}
