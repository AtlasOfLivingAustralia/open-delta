package au.org.ala.delta.translation.dist;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.dist.WriteOnceDistItemsFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.FilteredDataSet;

/**
 * Writes the key items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class DistItemsFileWriter {

	public static final int INAPPLICABLE_BIT = 20;
	private WriteOnceDistItemsFile _itemsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private ItemFormatter _itemFormatter;
	private BinaryKeyFileEncoder _encoder;
	
	
	public DistItemsFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			ItemFormatter itemFormatter,
			WriteOnceDistItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		_itemFormatter = itemFormatter;
		_encoder = new BinaryKeyFileEncoder();
		
	}
	
	public void writeAll() {
		
//		writeItems();
//		writeHeading();
//		writeCharacterMask();
//		writeNumbersOfStates();
//		writeCharacterDependencies();
//		writeCharacterReliabilities();
//		writeTaxonMask();
//		writeItemLengths();
//		writeItemAbundances();
//		
		// Need to write the header last as it is updated as each section 
		// is written.
		_itemsFile.writeHeader();
	}
	
	
	
}
