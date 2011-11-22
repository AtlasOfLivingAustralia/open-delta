package au.org.ala.delta.translation.parameter;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.PrintFile;

public abstract class ParameterTranslator {

	protected PrintFile _outputFile;
	protected String _terminator = ";";
	
	public ParameterTranslator(PrintFile outputFile) {
		_outputFile = outputFile;
	}
	
	public abstract void translateParameter(OutputParameter parameter);
	
	
	public void setTerminator(String terminator) {
		_terminator = terminator;
	}
	protected String comment(String comment) {
		StringBuilder commentBuffer = new StringBuilder();
		commentBuffer.append("[").append(comment).append("]");
		return commentBuffer.toString();
	}
	
	protected void command(String command) {
		_outputFile.outputLine(command+_terminator);
	}
}
