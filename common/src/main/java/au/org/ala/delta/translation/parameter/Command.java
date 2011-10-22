package au.org.ala.delta.translation.parameter;

import au.org.ala.delta.translation.PrintFile;

public class Command extends ParameterTranslator {
	private String _value;
	private int _trailingLines;
	
	private static final String COMMAND_TERMINATOR = ";";
	
	public Command(PrintFile outputFile, String value) {
		this(outputFile, value, 0);
	}
	
	public Command(PrintFile outputFile, String value, int trailingBlankLines) {
		super(outputFile);
		_value = value;
		_trailingLines = trailingBlankLines;
	}
	@Override
	public void translateParameter(String parameter) {
		_outputFile.outputLine(_value+COMMAND_TERMINATOR);
		_outputFile.writeBlankLines(_trailingLines, 0);
	}
}
