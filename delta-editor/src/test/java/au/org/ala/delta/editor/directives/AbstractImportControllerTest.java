package au.org.ala.delta.editor.directives;

import java.lang.reflect.Method;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.junit.After;
import org.junit.Before;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;

/**
 * Base class for test cases testing the import controller. Each subclass
 * works with a different data set.
 */
public abstract class AbstractImportControllerTest extends DeltaTestCase {

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
	protected ImportController importer;
	
	/** The data set we are importing into */
	protected SlotFileDataSet _dataSet;
	
	protected SlotFileRepository _repository;
	
	@Before
	public void setUp() throws Exception {
		
		DeltaEditorTestHelper helper = createTestHelper();
		_repository = new SlotFileRepository();
		createDataSet();
		EditorDataModel model = new EditorDataModel(_dataSet);
		helper.setModel(model);

		importer = new ImportController(helper, model);
	}
	
	protected void createDataSet() throws Exception {
		_dataSet = (SlotFileDataSet)_repository.newDataSet();
	}
	
	@After
	public void tearDown() throws Exception {
		_dataSet.close();
	}	
}
