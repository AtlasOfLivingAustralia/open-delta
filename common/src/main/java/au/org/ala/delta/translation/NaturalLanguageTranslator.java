package au.org.ala.delta.translation;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.Words.Word;

/**
 * Writes natural language description of a DeltaDataSet using the configuration
 * specified by the DeltaContext.
 */
public class NaturalLanguageTranslator {

	private TypeSetter _typeSetter;
	private DeltaContext _context;
	private Formatter _formatter;
	private DeltaDataSet _dataSet;

	public NaturalLanguageTranslator(DeltaContext context, TypeSetter typeSetter) {
		_context = context;
		_dataSet = context.getDataSet();
		_typeSetter = typeSetter;
		_formatter = new Formatter(context);
	}

	/**
	 * Translates the DeltaDataSet into a natural lanugage description.
	 */
	public void translate() {
		// TODO work out whether we need to output an "implicit attributes"
		// section.
		// if not, i think we need to explicitly include the implicit attributes
		// in the items...

		// Start of file mark.
		_typeSetter.insertTypeSettingMarks(28);

		DeltaDataSet dataSet = _context.getDataSet();

		int numItems = dataSet.getMaximumNumberOfItems();
		for (int i = 1; i <= numItems; i++) {
			translateItem(dataSet.getItem(i));
		}

		// End of file mark
		_typeSetter.insertTypeSettingMarks(29);
	}

	private boolean _printingImplicitAttributeSection;

	/** This is a static local variable in the fortran code */
	private int currentItemHeadingItemNumber;
	/** This is a static local variable in the fortran code */
	private int currentIndexHeadingItemNumber;

	enum TypeSetting {
		ADD_TYPESETTING_MARKS, DO_NOTHING, REMOVE_EXISTING_TYPESETTINGMARKS
	};

	TypeSetting _typeSettingMode = TypeSetting.DO_NOTHING;

	boolean _newParagraph;

	private void translateItem(Item item) {

		boolean ifBegin = false;
		boolean ifEnd = true;
		boolean chineseLanguageFormat = false;

		int itemNumber = item.getItemNumber();
		boolean isImplicitCharactersSection = item.getDescription().equals(
				"Implicit Characters");

		if (!isImplicitCharactersSection) {
			if (StringUtils.isNotEmpty(_context.getItemHeading(itemNumber))) {
				currentItemHeadingItemNumber = itemNumber; // ITHD
			}
			if (StringUtils.isNotEmpty(_context.getIndexHeading(itemNumber))) {
				currentIndexHeadingItemNumber = itemNumber; // IXHD
			}

			// If Item is excluded by the EXCLUDE ITEMS directive
			if (_context.isExcluded(item.getItemNumber())) {
				return;
			}
		}

		// Output a typesetting mark at the start of each file.
		if (!ifBegin) {
			_typeSetter.insertTypeSettingMarks(28);
			ifBegin = true;
			ifEnd = false;
		}

		// The "Implicit Characters" section ignores the ADD CHARACTERS and
		// EMPHASISE CHARACTERS
		// directive.
		if (!isImplicitCharactersSection) {
			// Copies IADDT(item number) into IADDC if ADD CHARACTERS directive
			// not null and
			// data exists for the item.

			// Otherwise initialise IADDC to all 0s.

			// Same again for EMPHAISE CHARACTERS - copies IEMPT(item number)
			// into IEMPC
			// Or initialises IEMPC to all zeros.

		} else {
			// initialises IEMPC and IADDC to all 0s.
		}

		// Setup indentation.
		int pSeq = 0;
		_typeSetter.setIndent(0);
		int numBlankLines;

		if (chineseLanguageFormat) {
			numBlankLines = 1;
		} else {
			numBlankLines = 2;
		}
		if (_typeSettingMode == TypeSetting.DO_NOTHING
				|| _typeSettingMode == TypeSetting.REMOVE_EXISTING_TYPESETTINGMARKS) {
			_typeSetter.writeBlankLines(numBlankLines, 5);
		} else {
			_typeSetter.writeBlankLines(1, 0);
			if (!isImplicitCharactersSection) {
				_typeSetter.insertTypeSettingMarks(13);
			}
		}

		printItemHeading(item.getItemNumber());
		printIndexHeading(item.getItemNumber());

		if (!_printingImplicitAttributeSection) {
			printTaxonName(item);
		}

		_newParagraph = false;
		if (_context.getNewParagraphCharacters().isEmpty()) {
			_newParagraph = true;
		}

		int lParaTxt = 1;
		boolean jiempf = false;
		int jiempc = 0;
		int ifeto = 0;
		int lstcho = 0;
		int iotxt = 0;
		int lastxt = 0;

		int[] ifofset = new int[_dataSet.getNumberOfCharacters()];
		Arrays.fill(ifofset, 0);

		// This loop fills in offsets for linked characters... whatever that
		// means....
		// MIght also setup buffers etc for printing.
		for (int i = 1; i < _dataSet.getNumberOfCharacters(); i++) {
			if (ifofset[i] != 0) {
				continue;
			}

		}

		int numChars = _context.getDataSet().getNumberOfCharacters();

		for (int i = 1; i <= numChars; i++) {

			int ichtxt = 0;
			int jstatout = 0;
			int icmtout = 0;
			int notoutp = 0;

			if (_context.startNewParagraphAtCharacter(i)) {
				_newParagraph = true;
			}
			if (_context.getItemSubheading(i) != null) {
				/* jhd = */_context.getItemSubheading(i);
			}
			if (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
				jiempf = _context.emphasizeFeature(i);
			}

			if (isIncluded(item, i) == 0) {
				continue;
			}
			Attribute attribute = _dataSet
					.getAttribute(item.getItemNumber(), i);
			if (attribute != null) {

				// Only start a new paragraph if we've output something since
				// the last paragraph
				if (_context.startNewParagraphAtCharacter(i)) {
					if (lParaTxt == 0) {
						_newParagraph = false;
					}
				}
				jiempc = 0;
				int jiemps = 0;
				if (_context.isCharacterEmphasized(item.getItemNumber(), i)) {
					jiempc = i;
					jiemps = 1;
				}

				if ((jiempc == 0)
						&& (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS)) {
					Set<Integer> linkedChars = _context.getLinkedCharacters(i);
					if (!linkedChars.isEmpty()) {
						int ifset = 0;
						if (ifofset[i] != 0) {
							ifset = 1;
						}
						int minof = 0;
						for (int j = i + 1; j < _dataSet
								.getNumberOfCharacters(); j++) {
							if (isCoded(item, j) && (isIncluded(item, j) > 0)) {
								if ((isIncluded(item, j) == 1)
										&& (true /*
												 * there is a check for a type
												 * of implicitness here - the
												 * character is missing. type 2
												 * means the character number is
												 * there but nothing else.
												 */)
										&& (_context.insertImplicitValues() == false)) {
									continue;
								}
								if (isUnknownOrInapplicable(attribute)) {
									continue;
								}
								if (_context.isCharacterEmphasized(
										item.getItemNumber(), j)
										&& ifofset[j] != 0) {
									jiempc = i;
									if (ifset != 0) {
										break;
									} else {
										if (ifofset[j] > 0) {
											if (minof == 0) {
												minof = ifofset[j];
											} else {
												minof = Math.min(minof,
														ifofset[j]);
											}
										}
									}
								}
							}
						}
						if ((jiempc != 0) && (ifset == 0)) {
							ifofset[i] = minof;
						}
					}
				}

				int ioa = 0;
				if (item.isVariant()) {
					if (!outputRedundantVariantAttribute(item, i)) {
						continue;
					}
				}
				if ((isIncluded(item, i) == 1) && attribute.isImplicit()
						&& !_context.insertImplicitValues()) {
					continue;
				}

				String comma = Words.word(Word.COMMA);
				if (_context.useAlternateComma()) {
					comma = Words.word(Word.ALTERNATE_COMMA);
				}

				/**
				 * Error case where there is nothing coded for the attribute
				 * (and presumably it's not implicit or part of a variant item?
				 * it seems to put ****** out. I've got some missing characters
				 * in my dataset and it doesn't do this. Not sure how to trigger
				 * this condition or if it's just an internal bug detection
				 * routine in CONFOR.
				 */

				// if just character number coded
				// continue; Not sure how we represent this case in our model.

				boolean useOr = !(_context.omitOrForCharacter(i));
				boolean useComma = !_context.replaceSemiColonWithComma(i);

				Character character = _context.getDataSet().getCharacter(i);

				writeFeature(character.getDescription(), true,
						item.getItemNumber(), i, _context.getItemSubheading(i),
						false, false, ifofset);
				writeCharacterAttributes(item, character);
			}
		}

	}

	private boolean isUnknownOrInapplicable(Attribute attribute) {

		return false;
	}

	private int _lastCharacterOutput;
	private int _previousCharInSentence;
	private boolean _characterOutputSinceLastPuntuation;
	private boolean _textOutputSinceLastParagraph;

	private void writeFeature(String description, boolean omitFinalPeriod,
			int itemNumber, int characterNumber, String subHeading,
			boolean emphasizeFeature, boolean emphasizeCharacter, int[] offsets) {

		// Insert a full stop if required.
		if (_newParagraph == true
				|| StringUtils.isNotEmpty(subHeading)
				|| (_previousCharInSentence != 0)
				|| (!_context.getLinkedCharacters(characterNumber).contains(
						_previousCharInSentence))) {

			if ((_previousCharInSentence != 0)
					&& (!_context
							.getOmitPeriodForCharacter(_lastCharacterOutput))) {
				_typeSetter.insertPunctuationMark(Word.FULL_STOP);
			}
			_previousCharInSentence = 0;
			_characterOutputSinceLastPuntuation = false;
		} else {
			// do we need to insert a ; or ,?
			Word punctuationMark = Word.SEMICOLON;
			if (_context.replaceSemiColonWithComma(characterNumber)
					&& _context.replaceSemiColonWithComma(_lastCharacterOutput)) {
				punctuationMark = Word.COMMA;
				if (_context.useAlternateComma()) {
					punctuationMark = Word.ALTERNATE_COMMA;
				}
			}
			if (_characterOutputSinceLastPuntuation) {
				writePunctuation(punctuationMark);
			}
		}

		if (_newParagraph == true) {
			if (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
				_typeSetter.endLine();
				_typeSetter.insertTypeSettingMarks(16);
			} else {
				_typeSetter.writeBlankLines(1, 2);
				_typeSetter.setIndent(6);
				_typeSetter.indent();
			}
			_newParagraph = false;
			_textOutputSinceLastParagraph = false;
		}

		if (StringUtils.isNotEmpty(subHeading)) {
			_typeSetter.insertTypeSettingMarks(32);
			writeSentence(subHeading, 0, 0);

			_typeSetter.insertTypeSettingMarks(33);
		}
		if (!_context.omitCharacterNumbers()) {
			_typeSetter.writeJustifiedOutput("(" + characterNumber + ")", 0,
					false);
		}

		int ioffset = 0;
		// Check if we are starting a new sentence or starting a new set of
		// linked characters.
		if ((_previousCharInSentence == 0)
				|| (_context.getLinkedCharacters(characterNumber).contains(
						_previousCharInSentence) && _lastCharacterOutput < _previousCharInSentence)) {
			ioffset = 0;
			_typeSetter.captialiseNextWord();
		} else {
			if (offsets[characterNumber] < 0) {
				return;
			}
			// TODO _typeSetter.dontCapitalizeNextWord();
		}
		int completionAction = -1;
		boolean emphasisApplied = false;
		if (emphasizeCharacter) {
			if ((ioffset == 0)
					|| _context.isCharacterEmphasized(itemNumber,
							characterNumber)) {
				_typeSetter.insertTypeSettingMarks(19);
				emphasisApplied = true;
				completionAction = -1;
				if (ioffset == 0 && (offsets[characterNumber] > 0)) {
					ioffset = -offsets[characterNumber];
				}
			}
		}
		description = _formatter.formatCharacterName(description);
		writeSentence(description, 0, completionAction);

		if (emphasizeCharacter) {
			if (emphasisApplied) {
				_typeSetter.insertTypeSettingMarks(20);
			}
			if (emphasizeFeature) {
				_typeSetter.insertTypeSettingMarks(18);
			}
		}

		_previousCharInSentence = characterNumber;

	}

	private void writeSentence(String sentence, int commentAction,
			int completionAction) {
		// TODO This does a bunch of stuff including inserting typesetting marks
		// in the middle
		// of the sentence.
		_typeSetter.writeJustifiedText(sentence, completionAction,
				commentAction == 4 || commentAction == 5);

	}

	private void writePunctuation(Word punctuationMark) {
		_typeSetter.insertPunctuationMark(Word.FULL_STOP);
		_characterOutputSinceLastPuntuation = false;
	}

	/**
	 * If the INSERT REDUNDANT VARIANT ATTRIBUTES directive has been given
	 * return true. If the OMIT REDUNDANT VARIANT ATTRIBUTES directive has been
	 * given return true only if: 1) The attribute has been coded. 2) The coded
	 * value is different to the value of the attribute in the master Item. If
	 * neither of these directives have been given, return true if the character
	 * has been added.
	 * 
	 * @return true if the attribute should be output.
	 */
	private boolean outputRedundantVariantAttribute(Item item, int character) {
		boolean masterItemMaskedIn = true; /*
											 * Not sure under what conditions
											 * this would be false
											 */
		if (masterItemMaskedIn) {
			Boolean omitRedundantVariantAttributes = _context
					.getOmitRedundantVariantAttributes();
			if (omitRedundantVariantAttributes == null) {
				if (_context.isCharacterAdded(item.getItemNumber(), character) == false) {
					// Don't output this attribute
					return false;
				}
			} else if (omitRedundantVariantAttributes == true) {
				if (_context.isCharacterAdded(item.getItemNumber(), character) == false) {
					// Don't output this attribute
					return false;
				}
				boolean attributeValueIsSameAsMasterItem = false;
				/** need to compare to master */
				if (attributeValueIsSameAsMasterItem /*
													 * this comparison is an
													 * artifact of how the data
													 * is stored
													 */) {
					// Don't output this attribute
					return false;
				}
			}
		}
		return true;
	}

	private boolean isCoded(Item item, int characterNumber) {
		Attribute attribute = _dataSet.getAttribute(item.getItemNumber(),
				characterNumber);

		// V is considered coded.
		// U is not unless some other subgroup exists (Maybe a comment?)
		// Inapplicable is considered coded if the OMIT INAPPLICABLES directed
		// has not been received.
		Character character = _dataSet.getCharacter(characterNumber);
		ControllingInfo huh = character.checkApplicability(item);
		huh.isInapplicable();
		// return (attribute != null && attribute.isVariable() ||
		// (attribute.isInapplicable() && _context.isOmitInapplicables() ==
		// false));
		return (attribute != null);
	}

	private int isIncluded(Item item, int characterNumber) {
		int result = 1;
		if (_context.isExcluded(characterNumber)) {
			result = 0;
			// if _context.isCharacterAdded(int item, int character) ||
			// _context.isEmphasized(int item, int character) {
			// result = 2;
			// }
		}

		return result;
	}

	private void translateCharacterDescription(Character character) {
		if (_context.getNewParagraphCharacters().contains(
				character.getCharacterId())) {
			_typeSetter.newParagraph();
		}

		String characterDescription = _formatter.formatCharacterName(character
				.getDescription());
		_typeSetter.captialiseNextWord();
		_typeSetter.writeJustifiedOutput(characterDescription, 0, false);
	}

	private void printItemHeading(int itemNumber) {

		String heading = _context.getItemHeading(itemNumber);
		if (heading == null) {
			return;
		}
		_typeSetter.insertTypeSettingMarks(30);
		writeSentence(heading, 0, 0);
		_typeSetter.insertTypeSettingMarks(31);
	}

	// Lines 857-882 of TNAT.FOR
	private void printIndexHeading(int itemNumber) {

		// ignoring for the moment....
	}

	private boolean _startOfNewFile = false;

	private void printTaxonName(Item item) {

		Integer characterForTaxonNames = _context.getCharacterForTaxonNames();

		int typeSettingMarkNum = 14;

		// if !_startOfNewFile || _already output item heading and value exists
		// for type mark 51
		if (!_startOfNewFile) {
			typeSettingMarkNum = 51;
		}

		if (characterForTaxonNames != null) {
			writeCharacterForTaxonName(item, characterForTaxonNames,
					typeSettingMarkNum, 0, true);
		} else {
			writeName(item, 1, typeSettingMarkNum, 0, true);
		}
		writeNameToIndexFile();

		_typeSetter.insertTypeSettingMarks(36);
		_typeSetter.endLine();

	}

	private void writeNameToIndexFile() {
	}

	private void writeCharacterAttributes(Item item, Character character) {
		if (item.hasAttribute(character)) {
			_typeSetter.writeJustifiedText(
					_formatter.formatAttribute(character, item
									.getAttribute(character).getValue()), -1,
					false);
		}

	}

	private void writeCharacterForTaxonName(Item item, int characterNumber,
			int typeSettingMarkNum, int completionAction,
			boolean masterItemMaskedIn) {
		if (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
			_typeSetter.insertTypeSettingMarks(typeSettingMarkNum);
		}

		if (item.isVariant() && masterItemMaskedIn) {
			// next character is a capital
			_typeSetter.captialiseNextWord();
			_typeSetter.writeJustifiedOutput(Words.word(Word.VARIANT), 0,
					false, false);
		}

		Attribute attribute = _dataSet.getAttribute(item.getItemNumber(),
				characterNumber);
		// TODO the CONFOR code (TNAT) strips comments. Not sure how nested
		// comments are treated
		// as I don't yet understand how item descriptions are broken into the
		// subgroups.
		_typeSetter.writeJustifiedOutput(attribute.getValue(), 0, false, false);

		if (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
			_typeSetter.insertTypeSettingMarks(15);
		}

		if (completionAction > 0) {
			_typeSetter.writeJustifiedOutput(" ", completionAction, false);
			if ((attribute == null)
					|| StringUtils.isEmpty(attribute.getValue())) {
				_typeSetter.writeBlankLines(1, 0);
			}
		}

	}

	/**
	 * 
	 * @param item
	 *            the item to write the name of
	 * @param commentAction
	 *            0 = omit comments, 1 = output comments with angle brackets, 2
	 *            - output comments without angle brackets
	 * @param typeSettingMarkNum
	 * @param completionAction
	 */
	private void writeName(Item item, int commentAction,
			int typeSettingMarkNum, int completionAction,
			boolean masterItemMaskedIn) {
		String description = item.getDescription();

		if ((commentAction != 0) || (false/*
										 * item description subgroup 1 type is
										 * not text comment
										 */)) {
			if (/* is text comment and */commentAction == 2) {
				// skip first "<"
			}

			_typeSetter.insertTypeSettingMarks(typeSettingMarkNum);
			if (item.isVariant() && masterItemMaskedIn) {
				// next character is a capital
				_typeSetter.captialiseNextWord();
				_typeSetter.writeJustifiedOutput(Words.word(Word.VARIANT), 0,
						false, false);
			}
			if (/* item subgroup type is not text comment */false) {
				_typeSetter.insertTypeSettingMarks(25);
			}
			_typeSetter.writeJustifiedText(description, 0, false);
			if (/* item subgroup type is not text comment */false) {
				_typeSetter.insertTypeSettingMarks(26);
			}
		}
		_typeSetter.insertTypeSettingMarks(15);
		if (completionAction > 0) {
			_typeSetter.writeJustifiedText(" ", completionAction, false);
			if (StringUtils.isEmpty(description)) {
				_typeSetter.writeBlankLines(1, 0);
			}
		}
	}

}
