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
package au.org.ala.delta;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.StateValueMatrix;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.util.Functor;
import au.org.ala.delta.util.Utils;

/**
 * Context associated with a set of DELTA input files.
 */
public class DeltaContext {

	private DeltaDataSet _dataSet;
	
	private Map<String, Object> _variables;
	private int _ListFilenameSize = 15;
	private List<String> _errorMessages = new ArrayList<String>();

	private TranslateType _translateType;
	private Item[] _items;
	private Set<Integer> _excludedCharacters = new HashSet<Integer>();
	private Set<Integer> _excludedItems = new HashSet<Integer>();
	
	private Set<Integer> _newParagraphCharacters = new HashSet<Integer>();
	private Map<Integer, String> _itemHeadings = new HashMap<Integer, String>();
	private Map<Integer, String> _itemSubHeadings = new HashMap<Integer, String>();
	private Set<Set<Integer>> _linkedCharacters = new HashSet<Set<Integer>>();

	private int _numberOfCharacters;
	private int _maxNumberOfStates;
	private int _maxNumberOfItems;
	private boolean _omitTypeSettingMarks = false;
	private int _printWidth;
	private boolean _replaceAngleBrackets;
	private boolean _omitCharacterNumbers = false;
	private boolean _omitInnerComments = false;
	private boolean _omitInapplicables = false;

	private Integer _characterForTaxonImages = null;

	private String _credits;

	private PrintStream _listStream;
	private PrintStream _errorStream;
	private PrintStream _printStream;
	private Stack<ParsingContext> _parsingContexts = new Stack<ParsingContext>();

	private StateValueMatrix _matrix;

	private double[] _characterWeights;
	
	public DeltaContext() {
		this(new DefaultDataSetFactory().createDataSet(""));
	}
	
	public DeltaContext(DeltaDataSet dataSet) {
		_variables = new HashMap<String, Object>();

		_variables.put("DATEFORMAT", "dd-MMM-yyyy");
		_variables.put("TIMEFORMAT", "HH:mm");

		_variables.put("DATE", new Functor() {
			@Override
			public Object invoke(DeltaContext context) {
				String dateFormat = (String) context.getVariable("DATEFORMAT", "dd-MMM-yyyy");
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				return sdf.format(new Date());
			}
		});

		_variables.put("TIME", new Functor() {

			@Override
			public Object invoke(DeltaContext context) {
				String timeFormat = (String) context.getVariable("TIMEFORMAT", "HH:mm");
				SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
				return sdf.format(new Date());
			}
		});

		_printStream = System.out;
		_errorStream = System.err;
		_listStream = System.out;
		
		_dataSet = dataSet;
	}
	
	private void init() {
		
	}
	
	
	public DeltaDataSet getDataSet() {
		return _dataSet;
	}

	public void setVariable(String name, Object value) {
		_variables.put(name.toUpperCase(), value);
	}

	public Object getVariable(String name, Object defvalue) {
		String key = name.toUpperCase();
		if (_variables.containsKey(key)) {
			Object val = _variables.get(key);
			if (val instanceof Functor) {
				return ((Functor) val).invoke(this);
			} else {
				return val;
			}
		} else {
			return defvalue;
		}
	}

	public void initializeMatrix() {
		assert getNumberOfCharacters() > 0;
		assert getMaximumNumberOfItems() > 0;
		_matrix = new StateValueMatrix(getNumberOfCharacters(), getMaximumNumberOfItems());
	}

	public StateValueMatrix getMatrix() {
		return _matrix;
	}

	public void setPrintStream(PrintStream stream) {
		_printStream = stream;
	}

	public void setErrorStream(PrintStream stream) {
		_errorStream = stream;
	}

	public void setListingStream(PrintStream stream) {
		_listStream = stream;
	}

	public void ListMessage(String line) {

		if (_listStream != null) {
			String prefix = "";
			if (_listStream == System.out) {
				prefix = "LIST:";
			}

			ParsingContext pc = getCurrentParsingContext();
			String filename = Utils.truncate(String.format("%s,%d", pc.getFile().getAbsolutePath(), pc.getCurrentLine()), _ListFilenameSize);
			OutputMessage(_listStream, "%s%s %s", prefix, filename, line);
		}
	}

	public void ErrorMessage(String format, Object... args) {
		String message = String.format(format, args);
		_errorMessages.add(message);
		OutputMessage(_errorStream, format, args);
	}

	public List<String> getErrorMessages() {
		return _errorMessages;
	}

	public void PrintMessage(String format, Object... args) {
		OutputMessage(_printStream, format, args);
	}

	private void OutputMessage(PrintStream stream, String format, Object... args) {
		if (stream != null) {
			if (args == null || args.length == 0) {
				stream.println(format);
			} else {
				String message = String.format(format, args);
				stream.println(message);
			}
		}
	}

	public void setCredits(String credits) {
		_credits = credits;
	}

	public String getCredits() {
		return _credits;
	}

	public void setNumberOfCharacters(int characters) {
		_numberOfCharacters = characters;
	}

	public int getNumberOfCharacters() {
		return _numberOfCharacters;
	}

	public ParsingContext getCurrentParsingContext() {
		if (_parsingContexts.size() > 0) {
			return _parsingContexts.peek();
		}
		return null;
	}

	public ParsingContext newParsingContext(File file) {
		ParsingContext context = new ParsingContext(file);
		_parsingContexts.push(context);
		return context;
	}

	public ParsingContext endCurrentParsingContext() {
		if (_parsingContexts.size() > 0) {
			return _parsingContexts.pop();
		}
		return null;
	}

	public void setMaximumNumberOfStates(int maxStates) {
		_maxNumberOfStates = maxStates;
	}

	public int getMaximumNumberOfStates() {
		return _maxNumberOfStates;
	}

	public void setMaximumNumberOfItems(int items) {
		_maxNumberOfItems = items;
		_items = new Item[items];
	}

	public int getMaximumNumberOfItems() {
		return _maxNumberOfItems;
	}

	public Character getCharacter(int number) {
		if (number <= _numberOfCharacters) {
			Character c = _dataSet.getCharacter(number);
			if (c==null) {
				c = _dataSet.addCharacter(number, CharacterType.UnorderedMultiState);
				((UnorderedMultiStateCharacter)c).setNumberOfStates(2);
			}
			return c;
		}
		throw new RuntimeException("No such character number " + number);
	}

	public Item getItem(int index) {
		return _dataSet.getItem(index);
	}

	public TranslateType getTranslateType() {
		return _translateType;
	}

	public void setTranslateType(TranslateType t) {
		_translateType = t;
	}

	public void setOmitTypeSettingMarks(boolean b) {
		_omitTypeSettingMarks = b;
	}

	public boolean isOmitTypeSettingMarks() {
		return _omitTypeSettingMarks;
	}

	public int getPrintWidth() {
		return _printWidth;
	}

	public void setPrintWidth(int width) {
		_printWidth = width;
	}

	public void setReplaceAngleBrackets(boolean b) {
		_replaceAngleBrackets = b;
	}

	public boolean isReplaceAngleBrackets() {
		return _replaceAngleBrackets;
	}

	public void setOmitCharacterNumbers(boolean b) {
		_omitCharacterNumbers = b;
	}

	public boolean isOmitCharacterNumbers() {
		return _omitCharacterNumbers;
	}

	public void setOmitInnerComments(boolean b) {
		_omitInnerComments = b;
	}

	public boolean isOmitInnerComments() {
		return _omitInnerComments;
	}

	public void setOmitInapplicables(boolean b) {
		_omitInapplicables = b;
	}

	public boolean isOmitInapplicables() {
		return _omitInapplicables;
	}

	public void setCharacterForTaxonImages(Integer charIdx) {
		_characterForTaxonImages = charIdx;
	}

	public Integer getCharacterForTaxonImages() {
		return _characterForTaxonImages;
	}

	public void excludeCharacter(int charIndex) {
		_excludedCharacters.add(charIndex);
	}

	public Set<Integer> getExcludedCharacters() {
		return _excludedCharacters;
	}

	public void newParagraphAtCharacter(int charIndex) {
		_newParagraphCharacters.add(charIndex);
	}

	public Set<Integer> getNewParagraphCharacters() {
		return _newParagraphCharacters;
	}

	public void setCharacterWeight(int number, double weight) {
		if (_characterWeights == null) {
			_characterWeights = new double[getNumberOfCharacters()];
		}
		_characterWeights[number-1] = weight;
		
	}
	
	public double getCharacterWeight(int number) {
		return _characterWeights[number-1];
	}

	/**
	 * Returns true if the specified item has been marked as excluded by an EXCLUDED ITEMS directive
	 * @param itemNumber the item number to check.
	 * @return whether the specified item is excluded.
	 */
	public boolean isExcluded(int itemNumber) {
		return _excludedItems.contains(itemNumber);
	}

	/**
	 * Returns the heading for the supplied item as defined by the ITEM HEADINGS directive.  If no heading has
	 * been supplied, this method returns null.
	 * @param itemNumber the item to get the heading for.
	 * @return the heading defined for the specified item or null if no heading was defined.
	 */
	public String getItemHeading(int itemNumber) {
		
		return _itemHeadings.get(itemNumber);
	}

	
	public void itemSubheading(int characterNumber, String subheading) {
		_itemSubHeadings.put(characterNumber, subheading);
		
	}
	
	/**
	 * Returns the sub heading for the supplied character as defined by the ITEM SUBHEADINGS directive.  
	 * If no heading has been supplied, this method returns null.
	 * @param characterNumber the character number to get the heading for.
	 * @return the heading defined for the specified item or null if no heading was defined.
	 */
	public String getItemSubheading(int characterNumber) {
		return _itemSubHeadings.get(characterNumber);
	}
	
	public void linkCharacters(Set<Integer> linkedCharacters) {
		_linkedCharacters.add(linkedCharacters);
	}
	
	/**
	 * Returns the set of characters linked to the supplied character.
	 * @param characterNumber the character of interest.
	 * @return the set of characters linked to the supplied character number, or null if the 
	 * character is not linked to any other characters.  The returned
	 * set includes the supplied character.
	 */
	public Set<Integer> getLinkedCharacters(int characterNumber) {
		for (Set<Integer> linkedCharacters : _linkedCharacters) {
			if (linkedCharacters.contains(characterNumber)) {
				return linkedCharacters;
			}
		}
		return null;
	}
}
