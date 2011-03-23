package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import junit.framework.TestCase;

import org.junit.Before;

import sun.awt.AppContext;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog.DirectiveFile;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.model.DeltaDataSet;

/**
 * Tests the ImportController class.
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
	
	
	/** The instance of the class we are testing */
	private ImportController importer;
	
	/** The data set we are importing into */
	private DeltaDataSet _dataSet;
	
	@Before
	public void setUp() {
		// Sure hope this won't throw a headless exception at some point...
		DeltaEditorTestHelper helper = new DeltaEditorTestHelper();
		_dataSet = new SlotFileDataSetFactory().createDataSet("test");
		EditorDataModel model = new EditorDataModel(_dataSet);
		helper.setModel(model);
		
		importer = new ImportController(helper);
	}
	
	public void testSilentImport() throws Exception {
		
		File datasetDirectory = new File(getClass().getResource("/dataset").toURI());
		DirectiveFile specs = new DirectiveFile("specs", DirectiveType.CONFOR);
		DirectiveFile chars = new DirectiveFile("chars", DirectiveType.CONFOR);
		DirectiveFile items = new DirectiveFile("items", DirectiveType.CONFOR);
		
		List<DirectiveFile> files = Arrays.asList(new DirectiveFile[] {specs, chars, items});
		
		importer.doSilentImport(datasetDirectory, files);
		
		// Because the import happens on a background (daemon) thread, we have to wait until 
		// the import is finished before doing our assertions.
		waitForTaskCompletion();
		
		assertEquals(89, _dataSet.getNumberOfCharacters());
		//assertEquals(14, _dataSet.getMaximumNumberOfItems());
		
		
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
