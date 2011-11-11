package au.org.ala.delta.translation.parameter;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.PrintFile;

public class Literal extends ParameterTranslator {
	private String _value;
	private int _trailingLines;
	
	public Literal(PrintFile outputFile, String value, int trailingLines) {
		super(outputFile);
		_value = value;
		_trailingLines = trailingLines;
	}
	
	@Override
	public void translateParameter(OutputParameter parameter) {
		_outputFile.outputLine(_value);
		_outputFile.writeBlankLines(_trailingLines, 0);
	}
}
