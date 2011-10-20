package au.org.ala.delta.directives;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.TextArgParser;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;

/**
 * Processes the OUTPUT PARAMETERS directive.
 */
public class OutputParameters extends AbstractCustomDirective {

	private DataSetTranslatorFactory _factory;

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
		
		au.org.ala.delta.translation.PrintFile output = context.getOutputFileSelector().getOutputFile();
		DataSetTranslator translator = _factory.createTranslator(context, output);
		for (String outputParameter : parser.getOutputParameters()) {
			translator.translateOutputParameter(outputParameter);
		}
	}

	private class OutputParametersParser extends DirectiveArgsParser {

		private static final char PARAMETER_IDENTIFIER = '#';
		private static final char PARAMETER_SEPARATOR = '\n';
		
		private List<String> _outputParameters;
		
		public OutputParametersParser(DeltaContext context, Reader reader) {
			super(context, reader);
			_outputParameters = new ArrayList<String>();
		}

		@Override
		public void parse() throws ParseException {
			
			readToNext(PARAMETER_SEPARATOR);
			while (_currentInt > 0) {
				String outputParameter = readOutputParameter();
				_outputParameters.add(outputParameter);	
			}
		}
		
		private String readOutputParameter() throws ParseException {
			// Consume the '\n'
			readNext();
			String line = readToNext(PARAMETER_SEPARATOR).trim();
			int parameterIndex = line.indexOf(PARAMETER_IDENTIFIER);
			if (parameterIndex >= 0) {
				int nextSpace = line.indexOf(" ", parameterIndex);
				if (nextSpace < 0) {
					nextSpace = line.length();
				}
				line = line.substring(parameterIndex, nextSpace);
			}
			
			return line;
		}
		
		public List<String> getOutputParameters() {
			return _outputParameters;
		}
		
	}
	
}
