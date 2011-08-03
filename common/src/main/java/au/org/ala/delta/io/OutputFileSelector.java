package au.org.ala.delta.io;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.rtf.RTFUtils;

public class OutputFileSelector {
	
	private int _characterForOutputFiles = 0;
	private Map<String, String> _itemOutputFiles = new HashMap<String, String>();
	private DeltaDataSet _dataSet;
	private String _subjectForOutputFiles;
	
	public OutputFileSelector(DeltaDataSet dataSet) {
		_dataSet = dataSet;
	}

	public String getOutputFile(int itemNumber) {
		if (_itemOutputFiles.isEmpty() && _characterForOutputFiles == 0) {
			throw new RuntimeException("One of ITEM OUTPUT FILES or CHARACTER FOR OUTPUT FILES must be specified.");
		}
		
		String outputFile = "";
		// This basically says use the current output file if a new one is
		// not specified for this item.  (to mimic CONFOR behaviour).
		while (itemNumber >= 1 && StringUtils.isEmpty(outputFile)) {
			if (_characterForOutputFiles == 0) {
				String description = itemDescriptionFor(itemNumber);
				outputFile = _itemOutputFiles.get(description);
			}
			else {
				Attribute attribute = _dataSet.getAttribute(itemNumber, _characterForOutputFiles);
				if (attribute != null) {
					outputFile = attribute.getValueAsString();
				}
			}
			itemNumber--;
		}
		return outputFile;
	}
	
	public void setSubjectForOutputFiles(String subjectForOutputFiles) {
		_subjectForOutputFiles = subjectForOutputFiles;
	}
	
	public String getSubjectForOutputFiles() {
		return _subjectForOutputFiles;
	}
	
	public void setItemOutputFile(String itemDescription, String fileName) {
		_itemOutputFiles.put(RTFUtils.stripFormatting(itemDescription), fileName);
	}
	
	private String itemDescriptionFor(int itemNumber) {
		Item item = _dataSet.getItem(itemNumber);
		String description = RTFUtils.stripFormatting(item.getDescription());
		return description;
	}
}
