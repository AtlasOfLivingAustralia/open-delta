package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

/**
 * Tests importing into an existing data set.
 */
public class ImportControllerTestWithExistingDataSet extends AbstractImportControllerTest {


	protected void createDataSet() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
		_dataSet = (SlotFileDataSet)_repository.findByName(f.getAbsolutePath(), null);
	}
	
	@Test
	public void testToKeyImport() throws Exception {
		String toKeyPath = "/au/org/ala/delta/editor/directives/expected_results";
		File datasetDirectory = new File(getClass().getResource(toKeyPath).toURI());
		DirectiveFileInfo tokey = new DirectiveFileInfo("tokey", DirectiveType.CONFOR);
		
		List<DirectiveFileInfo> files = Arrays.asList(new DirectiveFileInfo[] {tokey});
		
		int preImportCount = _dataSet.getDirectiveFileCount();
		
		importer.new DoImportTask(datasetDirectory, files).doInBackground();

//		assertEquals(preImportCount, _dataSet.getDirectiveFileCount());
//		
//		DirectiveFile file = _dataSet.getDirectiveFile(1);
//		
//		assertEquals(24, file.getDirectiveCount());
	}
	
}
