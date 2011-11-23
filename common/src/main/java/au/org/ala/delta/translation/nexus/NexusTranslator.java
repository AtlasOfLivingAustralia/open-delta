package au.org.ala.delta.translation.nexus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.FilteredCharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.key.KeyStateTranslator;
import au.org.ala.delta.translation.parameter.Command;
import au.org.ala.delta.translation.parameter.Literal;
import au.org.ala.delta.translation.parameter.ParameterBasedTranslator;
import au.org.ala.delta.translation.parameter.ParameterTranslator;
import au.org.ala.delta.translation.parameter.Specifications;
import au.org.ala.delta.translation.parameter.StateEncoder;
import au.org.ala.delta.translation.parameter.Symbols;

/**
 * Implements the translation into Nexus format as specified using the TRANSLATE
 * INTO NEXUS FORMAT directive.
 * PROCEDURE FOR DETERMINING NEXUS VALUES.
 * 1. NUMERIC CHARACTERS FOR WHICH KEY STATES HAVE NOT BEEN SPECIFIED ARE
 *   EXCLUDED.
 * 2. ONLY NORMAL VALUES OF NUMERIC CHARACTERS ARE USED. EXTREME VALUES
 *   ARE IGNORED.
 * 3. KEY STATES ARE APPLIED. ALL NUMERIC CHARACTERS ARE SUBSEQUENTLY TREATED
 *   AS ORDERED MULTISTATE.
 * 4. IF `USE MEAN VALUES' IS IN FORCE, MULTIPLE VALUES OF ORDERED MULTISTATES
 *   (INCLUDING FORMER NUMERICS) ARE REPLACED BY THEIR MEAN.
 * 5. THE VALUE OR VALUES ARE OUTPUT.

 */
public class NexusTranslator extends ParameterBasedTranslator {

	private enum PARAMETER {
		ASSUMPTIONS("#ASSUMPTIONS"), CHARLABELS("#CHARLABELS"), DATA("#DATA"), DIMENSIONS("#DIMENSIONS"), END("#END"), FORMAT(
				"#FORMAT"), HEADING("#HEADING"), STATELABELS("#STATELABELS"), MATRIX("#MATRIX"), NEXUS("#NEXUS"), TYPESET(
				"#TYPESET"), WTSET("#WTSET");

		private String _name;

		private PARAMETER(String name) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}

	};

	private static final int OUTPUT_COLUMNS = 80;
	private DeltaContext _context;
	private PrintFile _outputFile;
	private FilteredDataSet _dataSet;
	private CharacterFormatter _characterFormatter;
	private ItemFormatter _itemFormatter;
	private KeyStateTranslator _keyStateTranslator;

	public NexusTranslator(DeltaContext context, FilteredDataSet dataSet, 
			PrintFile outputFile, KeyStateTranslator keyStateTranslator,
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		if (_outputFile != null) {
			_outputFile.setWrapingGroupChars('\'', '\'');
			_outputFile.setLineWrapIndent(5);
			
		}
		_characterFormatter = characterFormatter;
		_itemFormatter = itemFormatter;
		_keyStateTranslator = keyStateTranslator;
		
		addParameters();
	}

	/**
	 * The output parameters accepted by a Nexus translation are:
	 * <ul>
	 * <li>ASSUMPTIONS</li>
	 * <li>CHARLABELS</li>
	 * <li>DATA</li>
	 * <li>DIMENSIONS</li>
	 * <li>END</li>
	 * <li>FORMAT</li>
	 * <li>HEADING</li>
	 * <li>STATELABELS</li>
	 * <li>MATRIX</li>
	 * <li>NEXUS</li>
	 * <li>TYPESET</li>
	 * <li>WTSET</li>
	 * </ul>
	 */
	public void addParameters() {
		
		ParameterTranslator translator = null;
		for (PARAMETER param : PARAMETER.values()) {
			switch (param) {
			case ASSUMPTIONS:
				translator = new Command(_outputFile, "BEGIN ASSUMPTIONS");
				break;
			case CHARLABELS:
				translator = new CharLabels(_outputFile);
				break;
			case DATA:
				translator = new Command(_outputFile, "BEGIN DATA");
				break;
			case DIMENSIONS:
				translator = new Specifications(_outputFile, _dataSet, "DIMENSIONS", "NTAX", "NCHAR", 1);
				break;
			case END:
				translator = new Command(_outputFile, "END");
				break;
			case FORMAT:
				translator = new Format(_outputFile);
				break;
			case HEADING:
				translator = new Heading(_outputFile);
				break;
			case STATELABELS:
				translator = new StateLabels(_outputFile);
				break;
			case MATRIX:
				translator = new Matrix(_outputFile);
				break;
			case NEXUS:
				translator = new Literal(_outputFile, "#NEXUS", 1);
				break;
			case TYPESET:
				translator = new TypeSet(_outputFile);
				break;
			case WTSET:
				translator = new WtSet(_outputFile);
				break;
			}
		
			addSupportedParameter(param.getName(), translator);
		}
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


	private static final int MAX_LENGTH = 30;
	private String truncate(String value) {
		return truncate(value, MAX_LENGTH);
	}
	class CharLabels extends ParameterTranslator {
		
		public CharLabels(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {

			_outputFile.outputLine("CHARLABELS");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while(characters.hasNext()) {
				outputCharacter(characters.next());
			}
			_outputFile.outputLine(";");
			_outputFile.writeBlankLines(1, 0);
		}
		
		private void outputCharacter(IdentificationKeyCharacter character) {
			FilteredCharacterFormatter _formatter = new FilteredCharacterFormatter();
			String description = _characterFormatter.formatCharacterDescription(character.getCharacter());
			StringBuilder charOut = new StringBuilder();
			charOut.append(comment(_formatter.formatCharacterNumber(character)));
			charOut.append(" ");
			charOut.append("'").append(truncate(description)).append("'");
			_outputFile.outputLine(charOut.toString());
			
		}
	}

	class Format extends Symbols {
		
		public Format(PrintFile outputFile) {
			super(outputFile, _dataSet, "SYMBOLS=", true, _context.getNumberStatesFromZero());
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			StringBuilder format = new StringBuilder();
			format.append("FORMAT MISSING=? GAP=- ");
			format.append(symbols());
			_outputFile.outputLine(format.toString());
			_outputFile.writeBlankLines(1, 0);
		}
	}

	class Heading extends ParameterTranslator {
		public Heading(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.outputLine(comment("!"+_context.getHeading(HeadingType.HEADING)));
		}
	}

	class StateLabels extends ParameterTranslator {
		public StateLabels(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.setIndentOnLineWrap(true);
			
			_outputFile.outputLine("STATELABELS");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while(characters.hasNext()) {
				outputCharacterStates(characters.next());
			}
			_outputFile.outputLine(";");
			_outputFile.writeBlankLines(1, 0);
			
			_outputFile.setIndentOnLineWrap(false);
			
		}
		
		private void outputCharacterStates(IdentificationKeyCharacter character) {
			StringBuilder states = new StringBuilder();
			states.append(character.getFilteredCharacterNumber());
			states.append(" ");
			boolean hasKeyStates = !character.getStates().isEmpty();
			for (int i=1; i<=character.getNumberOfStates(); i++) {
				states.append("'");
				String state = null;
				if (hasKeyStates) {
					state = _keyStateTranslator.translateState(character, i);
				}
				else {
					MultiStateCharacter multiStateChar = (MultiStateCharacter)character.getCharacter();
					state = _characterFormatter.formatState(multiStateChar, i, CommentStrippingMode.STRIP_ALL);
				}
				states.append(truncate(state));
				states.append("'");
				if (i != character.getNumberOfStates()) {
					states.append(" ");
				}
			}
			states.append(",");
			_outputFile.outputLine(states.toString());
		}
	}

	class Matrix extends ParameterTranslator {
		public Matrix(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.setTrimInput(false, true);
			_outputFile.outputLine("MATRIX ");
			_outputFile.setWrapingGroupChars('(', ')');
			_outputFile.setOutputFixedWidth(true);
			Iterator<FilteredItem> items = _dataSet.filteredItems();
			while (items.hasNext()) {
				Item item = items.next().getItem();
				Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
				StringBuilder statesOut = new StringBuilder();
				writeItem(item);
				while (characters.hasNext()) {
					IdentificationKeyCharacter character = characters.next();
					Attribute attribute = item.getAttribute(character.getCharacter());
					if (isInapplicable(attribute)) {
						statesOut.append("-");
					}
					else {
						List<Integer> states = new ArrayList<Integer>();
						
						if (attribute instanceof MultiStateAttribute) {
						    states.addAll(character.getPresentStates((MultiStateAttribute)attribute));
						}
						else if (attribute instanceof NumericAttribute) {
							states.addAll(character.getPresentStates((NumericAttribute)attribute));
						}
						
						addStates(statesOut, states);
					}
				}
				_outputFile.outputLine(pad(statesOut.toString(), OUTPUT_COLUMNS));
			}
			_outputFile.setOutputFixedWidth(false);
			_outputFile.outputLine(";");
			_outputFile.writeBlankLines(1, 0);
			_outputFile.setTrimInput(true);
		
		}
		
		private boolean isInapplicable(Attribute attribute) {
			if (!attribute.isExclusivelyInapplicable(true)) {
				ControllingInfo controllingInfo = _dataSet.checkApplicability(
						attribute.getCharacter(), attribute.getItem());
				return (controllingInfo.isInapplicable());
			}
			return true;
		}
		
		private void writeItem(Item item) {
			StringBuilder itemOut = new StringBuilder();
			itemOut.append("'");
			itemOut.append(_itemFormatter.formatItemDescription(item));
			itemOut.append("'");
			_outputFile.outputLine(pad(itemOut.toString(), OUTPUT_COLUMNS));
		}
		
		private void addStates(StringBuilder statesOut, List<Integer> states) {
			StateEncoder encoder = new StateEncoder(_context.getNumberStatesFromZero());
			if (states.size() == 0) {
				statesOut.append("?");
			}
			else if (states.size() > 1) {
				statesOut.append("(");
			}
			for (int state : states) {
				statesOut.append(encoder.encodeState(state));
			}
			if (states.size() > 1) {
				statesOut.append(")");
			}
		}
	}
	
	class WtSet extends ParameterTranslator {
		
		public WtSet(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			Map<BigDecimal, List<Integer>> weights = new TreeMap<BigDecimal, List<Integer>>();
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				BigDecimal weight = _context.getCharacterWeightAsBigDecimal(character.getCharacterNumber());
				if (!weights.containsKey(weight)) {
					List<Integer> chars = new ArrayList<Integer>();
					weights.put(weight, chars);
				}
				weights.get(weight).add(character.getFilteredCharacterNumber());
			}
			
			StringBuilder weightsOut = new StringBuilder();
			DeltaWriter writer = new DeltaWriter();
			weightsOut.append("WTSET * untitled =");
			for (BigDecimal weight : weights.keySet()) {
				weightsOut.append(" ");
				weightsOut.append(weight.toPlainString());
				weightsOut.append(": ");
				weightsOut.append(writer.rangeToString(weights.get(weight)));
			}
			command(weightsOut.toString());
			_outputFile.writeBlankLines(1, 0);
		}
	}

	class TypeSet extends ParameterTranslator {
		
		public TypeSet(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			List<Integer> unorderedMultiStateChars = new ArrayList<Integer>();
			List<Integer> orderedMultiStateChars = new ArrayList<Integer>();
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				CharacterType type = character.getCharacterType();
				if (type == CharacterType.UnorderedMultiState) {
					unorderedMultiStateChars.add(character.getFilteredCharacterNumber());
				}
				else {
					orderedMultiStateChars.add(character.getFilteredCharacterNumber());
				}
				
			}
			StringBuilder out = new StringBuilder();
			DeltaWriter writer = new DeltaWriter();
			
			out.append("TYPESET * untitled = "); 
			if (!unorderedMultiStateChars.isEmpty()) {
				out.append("unord: ");
				out.append(writer.rangeToString(unorderedMultiStateChars));
				if (!orderedMultiStateChars.isEmpty()) {
					out.append(", ");
				}
			}
			if (!orderedMultiStateChars.isEmpty()) {
				out.append("ord: ");
				out.append(writer.rangeToString(orderedMultiStateChars));
			}
			command(out.toString());
			_outputFile.writeBlankLines(1, 0);
			
		}
	}
}
