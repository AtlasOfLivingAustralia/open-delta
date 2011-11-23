package au.org.ala.delta.translation.paup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.parameter.Command;
import au.org.ala.delta.translation.parameter.ParameterBasedTranslator;
import au.org.ala.delta.translation.parameter.ParameterTranslator;
import au.org.ala.delta.translation.parameter.PaupHenningAttributes;
import au.org.ala.delta.translation.parameter.Specifications;
import au.org.ala.delta.translation.parameter.StateEncoder;
import au.org.ala.delta.translation.parameter.Symbols;

/**
 * Implements the translation into Paup format as specified using the TRANSLATE
 * INTO PAUP FORMAT directive.
 * 
 * KEY WORDS REQUIRED FOR PAUP SPECIFICATIONS.
 *     1 - COMMENT. 2 - PARAMETERS. 3 - SYMBOLS.
 *     4 - UNORDERED. 5 - DATA. 6 - GO. 7 - WEIGHTS. 8 - END.
 *
 * 
 * PROCEDURE FOR DETERMINING PAUP VALUES (SAME AS FOR HENNIG).
 * 1. NUMERIC CHARACTERS WITHOUT KEY STATES ARE EXCLUDED.
 * 2. ONLY `NORMAL' VALUES OF NUMERIC CHARACTERS ARE USED. EXTREME VALUES
 *    ARE IGNORED.
 * 3. KEY STATES ARE APPLIED. ALL NUMERIC CHARACTERS ARE SUBSEQUENTLY TREATED
 *    AS ORDERED MULTISTATE.
 * 4. IF A `USE MEAN VALUES' DIRECTIVE IS IN FORCE, MULTIPLE VALUES OF ORDERED
 *    MULTISTATE CHARACTERS (INCLUDING FORMER NUMERIC CHARACTERS) ARE REPLACED
 *    BY THEIR MEAN.
 * 5. IF ONLY ONE STATE VALUE IS PRESENT, THAT VALUE IS OUTPUT.
 * 6. IF ALL POSSIBLE STATE VALUES ARE PRESENT, OR IF MORE THAN ONE VALUE IS
 *    PRESENT AND `TREAT VARIABLE AS UNKNOWN' HAS BEEN SPECIFIED, THEN ?
 *    IS OUTPUT.
 *    OTHERWISE -
 * 7. FOR ORDERED MULTISTATE CHARACTERS (INCLUDING FORMER NUMERICS)
 *    A SINGLE VALUE IS OBTAINED AS IN STEP 4.
 * 8. FOR UNORDERED MULTISTATES, A SINGLE VALUE IS OBTAINED FROM THE ORIGINAL
 *    DATA (BEFORE THE APPLICATION OF KEY STATES) BY SELECTING THE FIRST VALUE
 *    CODED, UNLESS `USE LAST VALUE CODED' HAS BEEN SPECIFIED, WHEN THE LAST
 *    VALUE IS SELECTED. KEY STATES ARE THEN APPLIED IF SPECIFIED.
 * 9. THE VALUE IS OUTPUT.

 */
public class PaupTranslator extends ParameterBasedTranslator {

	private enum PARAMETER {
		COMMENT("#COMMENT"), PARAMETERS("#PARAMETERS"), SYMBOLS("#SYMBOLS"), UNORDERED("#UNORDERED"), DATA("#DATA"), 
		GO("#GO"), WEIGHTS("#WEIGHTS"), END("#END");

		private String _name;

		private PARAMETER(String name) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}

	};


	private static final int ITEM_NAME_LENGTH = 8;
	private static final int OUTPUT_COLUMNS = 80;
	
	private DeltaContext _context;
	private PrintFile _outputFile;
	private FilteredDataSet _dataSet;
	private ItemFormatter _itemFormatter;
	
	public PaupTranslator(DeltaContext context, FilteredDataSet dataSet, 
			PrintFile outputFile, 
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		if (_outputFile != null) {
			_outputFile.setWrapingGroupChars('\'', '\'');
			_outputFile.setLineWrapIndent(0);
			_outputFile.setIndent(0);
			_outputFile.setTrimInput(false, true);
			_outputFile.setPrintWidth(OUTPUT_COLUMNS);
		}
		_itemFormatter = itemFormatter;
		addParameters();
	}

	@Override
	protected void unrecognisedParameter(String parameter) {
		if (!parameter.startsWith("#")) {
			_outputFile.outputLine(parameter);
		}
		else {
			throw new IllegalArgumentException("Unsupported parameter: " + parameter);
		}
	}

	/**
	 * The output parameters accepted by a Paup translation are:
	 * <ul>
	 * <li>COMMENT</li>
	 * <li>PARAMETERS</li>
	 * <li>SYMBOLS</li>
	 * <li>UNORDERED</li>
	 * <li>DATA</li>
	 * <li>GO</li>
	 * <li>WEIGHTS</li>
	 * <li>END</li>
	 * </ul>
	 */
	public void addParameters() {
		ParameterTranslator translator = null;
		for (PARAMETER param : PARAMETER.values()) {
			switch (param) {
			case COMMENT:
				translator = new Comment(_outputFile);
				break;
			case PARAMETERS:
				translator = new Specifications(_outputFile, _dataSet, "PARAMETERS", "NOTU", "NCHAR", 0);
				break;
			case SYMBOLS:
				translator = new PaupSymbols(_outputFile, _context.getNumberStatesFromZero());
				break;
			case UNORDERED:
				translator = new Unordered(_outputFile);
				break;
			case DATA:
				translator = new Data(_outputFile);
				break;
			case GO:
				translator = new Command(_outputFile, "GO ");
				break;
			case WEIGHTS:
				translator = new Weights(_outputFile);
				break;
			case END:
				translator = new Command(_outputFile, "END ");
				break;
			}
		
			addSupportedParameter(param.getName(), translator);
		}

	}
	
	class Comment extends ParameterTranslator {
		public Comment(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.outputLine("!"+_context.getHeading(HeadingType.HEADING));
		}
	}

	class PaupSymbols extends Symbols {
		public PaupSymbols(PrintFile outputFile, boolean numberFromZero) {
			super(outputFile, _dataSet, "SYMBOLS ", false, numberFromZero);
		}

		@Override
		protected void writeSymbols(StringBuilder symbols, int first, int last) {
			symbols.append(StateEncoder.STATE_CODES[first]).append("-");
			if (last > 10 - first) {
				symbols.append(symbols.append(StateEncoder.STATE_CODES[10]));
				symbols.append(" ");
				symbols.append(StateEncoder.STATE_CODES[11]);
				if (last > 11 - first) {
					symbols.append("-");	
				}
			}
			symbols.append(StateEncoder.STATE_CODES[last - first+1]);
		}
	}
	
	/**
	 * Writes the DATA command (which includes the attribute data).
	 */
	class Data extends PaupHenningAttributes {
		public Data(PrintFile outputFile) {
			super(outputFile, _context, _dataSet, _itemFormatter, OUTPUT_COLUMNS, ITEM_NAME_LENGTH);
		}
		
		@Override
		public void translateParameter(OutputParameter parameter) {
			boolean nameOnNewLine = writeDataSpecification();
			writeAttributes(nameOnNewLine);
		}

		protected boolean writeDataSpecification() {
			StringBuilder data = new StringBuilder();
			// the output width seems to be ignored by the PAUP translation.
			data.append("DATA ");
			boolean itemNameOnNewLine = nameOnNewLine();
			int numChars = _dataSet.getNumberOfFilteredCharacters();
			if (itemNameOnNewLine) {
				data.append(String.format("(A%d,A1/(%dA1));", ITEM_NAME_LENGTH, Math.max(numChars, OUTPUT_COLUMNS)));
			}
			else {
				data.append(String.format("(A%d,A1,%dA1);", ITEM_NAME_LENGTH, numChars));
			}
			_outputFile.outputLine(data.toString());
			return itemNameOnNewLine;
		}
	}
	
	class Weights extends ParameterTranslator {
		
		public Weights(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			
			
			StringBuilder weightsOut = new StringBuilder();
			weightsOut.append("WEIGHTS");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			int count = 0;
			BigDecimal weight = new BigDecimal(-1);
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				BigDecimal tmpWeight = _context.getCharacterWeightAsBigDecimal(character.getCharacterNumber());
				
				if (!tmpWeight.equals(weight)) {
					outputWeight(weightsOut, count, weight);
					count = 0;
				}
				weight = tmpWeight;
				count++;
				
			}
			outputWeight(weightsOut, count, weight);
			command(weightsOut.toString());
		}
		protected void outputWeight(StringBuilder weightsOut, int count, BigDecimal weight) {
			if (count == 1) {
				weightsOut.append(" ").append(weight);
			}
			else if (count > 1) {
				weightsOut.append(" ").append(count).append("*").append(weight.toPlainString());
			}
		}
	}
	
	class Unordered extends ParameterTranslator {
		
		public Unordered(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			List<Integer> unorderedMultiStateChars = new ArrayList<Integer>();
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				CharacterType type = character.getCharacterType();
				if (type == CharacterType.UnorderedMultiState) {
					if (character.getNumberOfStates() > 2) {
						unorderedMultiStateChars.add(character.getFilteredCharacterNumber());
					}
				}
			}
			StringBuilder out = new StringBuilder();
			DeltaWriter writer = new DeltaWriter();
			
			out.append("UNORDERED  "); 
			
			out.append(writer.rangeToString(unorderedMultiStateChars));
			
			command(out.toString());
		}
	}

}
