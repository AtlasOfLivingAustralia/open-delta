package au.org.ala.delta.translation.parameter;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.PrintFile;

public class Specifications extends ParameterTranslator {

	private String _command;
	private String _itemsLabel;
	private String _charsLabel;
	private FilteredDataSet _dataSet;
	private int _blanks;
	
	public Specifications(
			PrintFile outputFile, FilteredDataSet dataSet, 
			String command, String itemsLabel, 
			String charsLabel, int blanks) {
		super(outputFile);
		_dataSet = dataSet;
		_command = command;
		_itemsLabel = itemsLabel;
		_charsLabel = charsLabel;
		_blanks = blanks;
	}

	@Override
	public void translateParameter(OutputParameter parameter) {
		StringBuilder specs = new StringBuilder();
		specs.append(_command).append(" ");
		if (StringUtils.isNotBlank(_itemsLabel)) {
			specs.append(_itemsLabel).append("=");
		}
		specs.append(_dataSet.getNumberOfFilteredItems());
		specs.append(" ");
		if (StringUtils.isNotBlank(_charsLabel)) {
			specs.append(_charsLabel).append("=");
		}
		specs.append(_dataSet.getNumberOfFilteredCharacters());
		command(specs.toString());
		if (_blanks > 0) {
			_outputFile.writeBlankLines(_blanks, 0);
		}
	}

}
