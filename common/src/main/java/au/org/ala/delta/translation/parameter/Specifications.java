package au.org.ala.delta.translation.parameter;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.PrintFile;

public class Specifications extends ParameterTranslator {

	private String _command;
	private String _itemsLabel;
	private String _charsLabel;
	private FilteredDataSet _dataSet;
	
	public Specifications(PrintFile outputFile, FilteredDataSet dataSet, String command, String itemsLabel, String charsLabel) {
		super(outputFile);
		_dataSet = dataSet;
		_command = command;
		_itemsLabel = itemsLabel;
		_charsLabel = charsLabel;
	}

	@Override
	public void translateParameter(OutputParameter parameter) {
		StringBuilder specs = new StringBuilder();
		specs.append(_command).append(" ");
		specs.append(_itemsLabel).append("=").append(_dataSet.getNumberOfFilteredItems());
		specs.append(" ");
		specs.append(_charsLabel).append("=").append(_dataSet.getNumberOfFilteredCharacters());
		command(specs.toString());
		_outputFile.writeBlankLines(1, 0);
	}

}
