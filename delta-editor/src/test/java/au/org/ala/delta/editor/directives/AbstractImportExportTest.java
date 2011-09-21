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

public abstract class AbstractImportExportTest extends DeltaTestCase {

	/** The data set we are importing into */
	protected SlotFileDataSet _dataSet;
	protected DeltaEditorTestHelper _helper;
	protected EditorDataModel _model;

	/**
	 * Allows us to manually set the data set to be returned from the
	 * getCurrentDataSet method.
	 */
	class DeltaEditorTestHelper extends DeltaEditor {
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

	protected SlotFileRepository _repository;

	public AbstractImportExportTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		_helper = createTestHelper();
		_repository = new SlotFileRepository();
		createDataSet();
		_model = new EditorDataModel(_dataSet);
		_helper.setModel(_model);

	}

	@After
	public void tearDown() throws Exception {
		_dataSet.close();
	}
	
	protected abstract void createDataSet() throws Exception;

}