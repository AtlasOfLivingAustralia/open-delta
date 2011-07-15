package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;

/**
 * Tests the DirectiveFileImporter class. 
 */
public class DirectiveFileImporterTest extends TestCase {

	/** The instance of the class we are testing */
	private DirectiveFileImporter _importer;
	
	/** The data set we are importing into */
	private SlotFileDataSet _dataSet;
	
	private ImportContext _context;
	
	private DirectiveImportHandlerStub _importHandler;
	
	
	class DirectiveImportHandlerStub implements DirectiveImportHandler {

		@Override
		public void preProcess(String data) {
			System.out.println("preProcess"+data);
			
		}

		@Override
		public void postProcess(
				AbstractDirective<? extends AbstractDeltaContext> directive) {
			System.out.println("postProcess"+directive);
			
		}

		@Override
		public void handleUnrecognizedDirective(ImportContext context,
				List<String> controlWords) {
			System.err.println("handleUnrecognizedDirective"+controlWords);
		}

		@Override
		public void handleDirectiveProcessingException(ImportContext context,
				AbstractDirective<ImportContext> d, Exception ex) {
			System.err.println("handleDirectiveProcessingException"+ex.getMessage());
		}
	}
	
	@Before
	public void setUp() throws Exception {
		
		_dataSet = (SlotFileDataSet)new SlotFileDataSetFactory().createDataSet("test");
		_context = new ImportContext(_dataSet);
		_importHandler = new DirectiveImportHandlerStub();
		_importer = new DirectiveFileImporter(_importHandler);
	}
	
	@Test
	public void testToIntImport() throws Exception {
		String toIntPath = "/au/org/ala/delta/editor/directives/expected_results/toint";
		File toint = new File(getClass().getResource(toIntPath).toURI());
		
		DirectiveFile file = _dataSet.addDirectiveFile(1, "toint", 0);
		
		_context.setDirectiveFile(file);
		_importer.parse(toint, _context);

		assertEquals(1, _dataSet.getDirectiveFileCount());
		
		file = _dataSet.getDirectiveFile(1);
		
		assertEquals(24, file.getDirectiveCount());
	}
	
}
