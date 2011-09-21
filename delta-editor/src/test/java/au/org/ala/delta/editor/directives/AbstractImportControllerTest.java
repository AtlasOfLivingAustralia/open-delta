package au.org.ala.delta.editor.directives;



import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

/**
 * Base class for test cases testing the import controller. Each subclass
 * works with a different data set.
 */
public abstract class AbstractImportControllerTest extends AbstractImportExportTest {

	
	/** The instance of the class we are testing */
	protected ImportController importer;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		importer = new ImportController(_helper, _model);
	}
	
	protected void createDataSet() throws Exception {
		_dataSet = (SlotFileDataSet)_repository.newDataSet();
	}	
}
