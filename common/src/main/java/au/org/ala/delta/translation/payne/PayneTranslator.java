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
package au.org.ala.delta.translation.payne;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.key.KeyStateTranslator;
import au.org.ala.delta.translation.parameter.Command;
import au.org.ala.delta.translation.parameter.ParameterBasedTranslator;
import au.org.ala.delta.translation.parameter.ParameterTranslator;
import au.org.ala.delta.translation.parameter.Specifications;

/**
 * Implements the translation into Payne format as specified using the TRANSLATE
 * INTO PAYNE FORMAT directive.
 * 
 * Supported output parameters are: 1 - CAPTION. 2 - COSTS. 3 - LEVELS. 4 -
 * NAMES. 5 - NUMBERS. 6 - PREFERENCES. 7 - PRIOR. 8 - RESULTS. 9 - CHARACTERS.
 */
public class PayneTranslator extends ParameterBasedTranslator {

	private enum PARAMETER {
		CAPTION("#CAPTION"), COSTS("#COSTS"), LEVELS("#LEVELS"), NAMES("#NAMES"), NUMBERS("#NUMBERS"), PREFERENCES(
				"#PREFERENCES"), PRIOR("#PRIOR"), RESULTS("#RESULTS"), CHARACTERS("#CHARACTERS");

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
	private ItemFormatter _itemFormatter;
	private CharacterFormatter _characterFormatter;
	private KeyStateTranslator _keyStateTranslator;


	public PayneTranslator(DeltaContext context, FilteredDataSet dataSet, PrintFile outputFile,
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter,
			KeyStateTranslator keyStateTranslator) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		if (_outputFile != null) {
			_outputFile.setLineWrapIndent(0);
			_outputFile.setIndent(0);
			_outputFile.setPrintWidth(OUTPUT_COLUMNS);
			_outputFile.setTrimInput(false, true);
		}
		_itemFormatter = itemFormatter;
		_characterFormatter = characterFormatter;
		_keyStateTranslator = keyStateTranslator;
		_matchLength = 3;
		addParameters();
	
	}

	@Override
	protected void unrecognisedParameter(String parameter) {
		if (!parameter.startsWith("#")) {
			_outputFile.outputLine(parameter);
		} else {
			throw new IllegalArgumentException("Unsupported parameter: " + parameter);
		}
	}

	/**
	 * The output parameters accepted by a Paup translation are:
	 * <ul>
	 * <li>CAPTION</li>.
	 * <li>COSTS</li>.
	 * <li>LEVELS</li>.
	 * <li>NAMES</li>.
	 * <li>NUMBERS</li>.
	 * <li>PREFERENCES</li>.
	 * <li>PRIOR</li>.
	 * <li>RESULTS</li>.
	 * <li>CHARACTERS</li>.
	 * </ul>
	 */
	public void addParameters() {
		ParameterTranslator translator = null;
		for (PARAMETER param : PARAMETER.values()) {
			switch (param) {
			case CAPTION:
				translator = new Heading(_outputFile);
				break;
			case COSTS:
				translator = new Costs(_outputFile, "COSTS");
				break;
			case LEVELS:
				translator = new Levels(_outputFile);
				break;
			case NAMES:
				translator = new Names(_outputFile);
				break;
			case NUMBERS:
				translator = new Specifications(_outputFile, _dataSet, "NUMBERS", "", "", 0);
				break;
			case PREFERENCES:
				translator = new Preferences(_outputFile, "PREFERENCES");
				break;
			case PRIOR:
				translator = new Command(_outputFile, "");
				break;
			case RESULTS:
				translator = new Results(_outputFile);
				break;
			case CHARACTERS:
				translator = new Characters(_outputFile);
				break;
			}
			translator.setTerminator("");
			addSupportedParameter(param.getName(), translator);
		}
	}

	
	class Characters extends ParameterTranslator {

		public Characters(PrintFile outputFile) {
			super(outputFile);
		}

		@Override
		public void translateParameter(OutputParameter parameter) {
			
			_outputFile.setOutputFixedWidth(true);
			_outputFile.outputLine(" ");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				_outputFile.outputLine(_characterFormatter.formatCharacterDescription(character.getCharacter())+":");
			}
			
			// Now output the states.
			characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				outputCharacterStates(character);
			}
			_outputFile.setOutputFixedWidth(false);
		}
		
		private void outputCharacterStates(IdentificationKeyCharacter character) {
			boolean hasKeyStates = !character.getStates().isEmpty();
			if (character.getNumberOfStates() < 2) {
				_outputFile.outputLine("A:");
				_outputFile.outputLine("B:");
			} 
			else {
				for (int i=1; i<=character.getNumberOfStates(); i++) {
					
					String state = null;
					if (hasKeyStates) {
						state = _keyStateTranslator.translateState(character, i);
					}
					else {
						MultiStateCharacter multiStateChar = (MultiStateCharacter)character.getCharacter();
						state = _characterFormatter.formatState(multiStateChar, i, CommentStrippingMode.STRIP_ALL);
					}
					
					_outputFile.outputLine(state+":");
				}
			}
			
		}
		
	}
	
	
	
	class Names extends ParameterTranslator {
		public Names(PrintFile outputFile) {
			super(outputFile);
		}
		
		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.outputLine("NAMES 3 :");
			_outputFile.setOutputFixedWidth(true);
			Iterator<FilteredItem> items = _dataSet.filteredItems();
			while (items.hasNext()) {
				Item item = items.next().getItem();
				_outputFile.outputLine(_itemFormatter.formatItemDescription(item)+":");
			}
			_outputFile.setOutputFixedWidth(false);
		}
	}

	class Results extends ParameterTranslator {
		public Results(PrintFile outputFile) {
			super(outputFile);
		}

		@Override
		public void translateParameter(OutputParameter parameter) {

			_outputFile.outputLine("RESULTS ");
			_outputFile.setOutputFixedWidth(true);
			Iterator<FilteredItem> items = _dataSet.filteredItems();
			while (items.hasNext()) {
				Item item = items.next().getItem();
				Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
				StringBuilder statesOut = new StringBuilder();
				while (characters.hasNext()) {
					IdentificationKeyCharacter character = characters.next();
					Attribute attribute = item.getAttribute(character.getCharacter());
					if (isInapplicable(attribute)) {
						statesOut.append("-1 ");
					} else {
						List<Integer> states = new ArrayList<Integer>();

						if (attribute instanceof MultiStateAttribute) {
							states.addAll(character.getPresentStates((MultiStateAttribute) attribute));
						} else if (attribute instanceof NumericAttribute) {
							states.addAll(character.getPresentStates((NumericAttribute) attribute));
						}

						addStates(statesOut, states);
					}
				}
				_outputFile.outputLine(statesOut.toString());
			}
			_outputFile.setOutputFixedWidth(false);
		}
		
		private boolean isInapplicable(Attribute attribute) {
			if (!attribute.isExclusivelyInapplicable(true)) {
				ControllingInfo controllingInfo = _dataSet.checkApplicability(
						attribute.getCharacter(), attribute.getItem());
				return (controllingInfo.isInapplicable());
			}
			return true;
		}
		
		private void addStates(StringBuilder statesOut, List<Integer> states) {
			
			if (states.size() == 0) {
				statesOut.append("0 ");
			}
			else if (states.size() > 1) {
				statesOut.append("/ ");
			}
			for (int state : states) {
				statesOut.append(state).append(" ");
			}
			if (states.size() > 1) {
				statesOut.append(": ");
			}
		}
	}

	class Heading extends ParameterTranslator {
		public Heading(PrintFile outputFile) {
			super(outputFile);
		}

		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.outputLine("CAPTION " + _context.getHeading(HeadingType.HEADING)+":");
		}
	}

	class Levels extends ParameterTranslator {
		public Levels(PrintFile outputFile) {
			super(outputFile);
		}

		@Override
		public void translateParameter(OutputParameter parameter) {
			Iterator<IdentificationKeyCharacter> chars = _dataSet.identificationKeyCharacterIterator();
			StringBuilder levels = new StringBuilder("LEVELS ");
			while (chars.hasNext()) {
				IdentificationKeyCharacter character = chars.next();
				int numStates = character.getNumberOfStates();
				if (numStates < 2) {
					numStates = 2;
				}
				levels.append(numStates);
				
				levels.append(" ");
			}
			_outputFile.outputLine(levels.toString());
		}
	}

	class Costs extends ParameterTranslator {

		private String _command;
		public Costs(PrintFile outputFile, String command) {
			super(outputFile);
			_command = command;
		}

		@Override
		public void translateParameter(OutputParameter parameter) {

			StringBuilder costsOut = new StringBuilder();
			costsOut.append(_command);
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				int reliability = 0;
				if (character.getNumberOfStates() >= 2 && !_context.isCharacterExcluded(character.getCharacter().getCharacterId())) {
				    reliability = (int)Math.round(_context.getCharacterReliability(character.getCharacterNumber()));
				}
				int cost = 10-reliability;
				
				costsOut.append(" ").append(cost);
			}
			
			_outputFile.outputLine(costsOut.toString()+" ");
			
		}
	}

	class Preferences extends Costs {

		public Preferences(PrintFile outputFile, String command) {
			super(outputFile, command);
		}
		
		@Override
		public void translateParameter(OutputParameter parameter) {
			super.translateParameter(parameter);
			_outputFile.outputLine("USE 9");
		}
	}
}
