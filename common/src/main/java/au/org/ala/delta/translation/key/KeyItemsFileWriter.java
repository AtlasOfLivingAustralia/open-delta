package au.org.ala.delta.translation.key;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.key.WriteOnceKeyCharsFile;
import au.org.ala.delta.key.WriteOnceKeyItemsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;

/**
 * Writes the key items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class KeyItemsFileWriter {

	private WriteOnceKeyItemsFile _itemsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	
	public KeyItemsFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			WriteOnceKeyItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		
	}
	
	public void writeAll() {
		
		
		// Need to write the header last as it is updated as each section 
		// is written.
		_itemsFile.writeHeader();
	}
	
	

}
