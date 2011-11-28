/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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
