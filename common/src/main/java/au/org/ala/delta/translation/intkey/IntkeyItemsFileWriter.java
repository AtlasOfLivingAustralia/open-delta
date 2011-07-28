package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.model.DeltaDataSet;

/**
 * Writes the intkey items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class IntkeyItemsFileWriter {

	private WriteOnceIntkeyItemsFile _itemsFile;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	public IntkeyItemsFileWriter(DeltaContext context, WriteOnceIntkeyItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = context.getDataSet();
		_context = context;
	}
	
	public void writeItemDescrptions() {
		
		List<String> descriptions = new ArrayList<String>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			descriptions.add(_dataSet.getItem(i).getDescription());
		}
		_itemsFile.writeItemDescrptions(descriptions);
	}
	
	public void writeCharacterSpecs() {
		throw new NotImplementedException();
	}
	
	public void writeMinMaxValues() {
		throw new NotImplementedException();
	}
	
	public void writeCharacterDependencies() {
		throw new NotImplementedException();
	}
	
	public void writeAttributeData() {
		throw new NotImplementedException();
	}
	
	
	public void writeKeyStateBoundaries() {
		throw new NotImplementedException();
	}
	
	public void writeTaxonImages() {
		throw new NotImplementedException();
	}
	
	public void writeEnableDeltaOutput() {
		throw new NotImplementedException();
	}
	
	public void writeChineseFormat() {
		throw new NotImplementedException();
	}
	
	public void writeCharacterSynonomy() {
		throw new NotImplementedException();
	}
	
	public void writeOmitOr() {
		throw new NotImplementedException();
	}
	
	public void writeUseControllingFirst() {
		throw new NotImplementedException();
	}

	public void writeTaxonLinks() {
		throw new NotImplementedException();
	}
	
	public void writeOmitPeriod() {
		throw new NotImplementedException();
	}
	
	public void writeNewParagraph() {
		throw new NotImplementedException();
	}
	
	public void writeNonAutoControllingChars() {
		throw new NotImplementedException();
	}
	
	public void writeSubjectForOutputFiles() {
		throw new NotImplementedException();
	}
	
	
}
