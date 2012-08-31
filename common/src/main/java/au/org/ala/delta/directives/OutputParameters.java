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
package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.TextArgParser;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes the OUTPUT PARAMETERS directive.
 */
public class OutputParameters extends AbstractCustomDirective {

	private DataSetTranslatorFactory _factory;

	public static class OutputParameter {
		
		public OutputParameter(String parameter, String fullText) {
			this.parameter = parameter;
			this.fullText = fullText;
		}
		public String parameter;
		public String fullText;
	}
	
	public OutputParameters() {
		super("output", "parameters");
		_factory = new DataSetTranslatorFactory();
	}
	
	
	@Override
	protected DirectiveArgsParser createParser(DeltaContext context, StringReader reader) {
		return new TextArgParser(context, reader);
	}


	/**
	 * The directive is stored as a text directive but processed specially.
	 */
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXT;
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String data = directiveArguments.getFirstArgumentText();
		OutputParametersParser parser = new OutputParametersParser(context, new StringReader(data));
		parser.parse();
		
		DataSetTranslator translator = _factory.createTranslator(context, DataSetTranslator.TranslationPhase.OUTPUT_PARAMETERS);
		for (OutputParameter outputParameter : parser.getOutputParameters()) {
			translator.translateOutputParameter(outputParameter);
		}
	}

	private class OutputParametersParser extends DirectiveArgsParser {

		private static final char PARAMETER_IDENTIFIER = '#';
		private static final char PARAMETER_SEPARATOR = '\n';
		
		private List<OutputParameter> _outputParameters;
		
		public OutputParametersParser(DeltaContext context, Reader reader) {
			super(context, reader);
			_outputParameters = new ArrayList<OutputParameter>();
		}

		@Override
		public void parse() throws ParseException {
			
			readToNext(PARAMETER_SEPARATOR);
			while (_currentInt > 0) {
				OutputParameter outputParameter = readOutputParameter();
				_outputParameters.add(outputParameter);	
			}
		}
		
		private OutputParameter readOutputParameter() throws ParseException {
			// Consume the '\n'
			readNext();
			String line = readToNext(PARAMETER_SEPARATOR).trim();
			String parameter = "";
			int parameterIndex = line.indexOf(PARAMETER_IDENTIFIER);
			if (parameterIndex >= 0) {
				int nextSpace = line.indexOf(" ", parameterIndex);
				if (nextSpace < 0) {
					nextSpace = line.length();
				}
				parameter = line.substring(parameterIndex, nextSpace);
			}
			
			return new OutputParameter(parameter, line);
		}
		
		public List<OutputParameter> getOutputParameters() {
			return _outputParameters;
		}
		
	}
	
}
