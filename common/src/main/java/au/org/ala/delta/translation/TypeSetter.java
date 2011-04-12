package au.org.ala.delta.translation;

import java.io.PrintStream;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.Words.Word;

/**
 * The TypeSetter is responsible for formatting output produced by the NaturalLanguageTranslator.
 */
public class TypeSetter {

	private Logger logger = Logger.getLogger(TypeSetter.class.getName());
	private static final String BLANK = " ";
	private int _printWidth = 80;
	private PrintStream _output;
	private int _currentIndent;
	private int _currentLinePos;
	private int _icmd;
	private boolean _capitalise;
	
	/** TODO fix me this is OUTPUT FORMAT HTML */
	private boolean _ihtml = false;
	
	private StringBuilder _outputBuffer;
	private int _endWordIndex;
	private boolean _indented;
	
	public TypeSetter(PrintStream output, int lineWidth) {
		_output = output;
		_outputBuffer = new StringBuilder();
		_indented = false;
		_printWidth = lineWidth;

	}

	public void insertTypeSettingMarks(int number) {

	}

	public void setIndent(int numSpaces) {

		_currentIndent = numSpaces;
	}

	/**
	 * Indents the default amount for the current output type. (takes a luntype
	 * and a number of spaces)
	 */
	public void indent() {
		if (_indented) {
			return;
		}
		if (_currentIndent <= (Math.abs(_printWidth) - 20) ) {
			for (int i = 0; i < _currentIndent; i++) {
				_outputBuffer.append(' ');
			}
		}
		_endWordIndex = 0;
		_indented = true;
	}

	public void endLine() {
		printBufferLine(false);
	}
	
	public void writeBlankLines(int numLines, int requiredNumLinesLeftOnPage) {
		
		printBufferLine();
		for (int i = 0; i < numLines; i++) {
			_output.println();
		}
	}
	
	public void printBufferLine() {
		printBufferLine(false);
	}
	
	public void printBufferLine(boolean indentNewLine) {
	
		_output.println(_outputBuffer.toString());
		_indented = false;
		_outputBuffer = new StringBuilder();
		if (indentNewLine) {
			indent();
		}
	}
	
	public void writeJustifiedText(String text, int completionAction, boolean inHtmlRtf) {
		
		text = text.trim();
		
		if (_capitalise) {
			text = capitaliseFirstWord(text);
		}
		
		System.out.println("Buffer :"+text);
		if (willFitOnLine() == false) {
			printBufferLine();
		}
		
		// Insert a space if one is required.
		if (_outputBuffer.length() > 0 && lastCharInBuffer() != ' ' && !isPunctuationMark(text)) {
			_outputBuffer.append(' ');
		}
		
		_outputBuffer.append(text);
		
		while (willFitOnLine() == false) {
			int numSpaces = numLeadingSpaces(_outputBuffer);
			int wrappingPos = _outputBuffer.lastIndexOf(" ", _printWidth);
			if (wrappingPos <= numSpaces) {
				wrappingPos = _printWidth;
			}
			
			String trailingText = _outputBuffer.substring(wrappingPos);
			_outputBuffer.delete(wrappingPos, _outputBuffer.length());
			printBufferLine();
			
			_outputBuffer.append(trailingText.trim());
		}
		complete(completionAction);
		
	}
	
	private boolean isPunctuationMark(String text) {
		return ".".equals(text) || ",".equals(text) || ";".equals(text);
	}
	
	private void complete(int completionAction) {
		if ((completionAction == 0) && (willFitOnLine())) {
			_outputBuffer.append(' ');
		}
		else if (completionAction > 0) {
			writeBlankLines(completionAction, 0);
		}
	}
	
	/**
	 * Returns the number of leading spaces in the supplied text.
	 * @param text the text to count leading spaces of.
	 * @return the number of leading spaces in the supplied text or zero if the parameter is null.
	 */
	private int numLeadingSpaces(CharSequence text) {
		if ((text == null) || (text.length() == 0)) {
			return 0;
		}
		int numSpaces = 0;
		while (text.charAt(numSpaces) == ' ') {
			numSpaces++;
		}
		return numSpaces;
	}

	/**
	 *  Capitalises the first word in the supplied text (which may contain RTF markup)
	 *	the first letter of the word is preceded by a '|'.
	 * @param text the text to capitalise.
	 * @return the text with the first word capitalised.
	 */
	public String capitaliseFirstWord(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}
		//
		StringBuilder tmp = new StringBuilder();
		tmp.append(text);
		int index = 0;
		if (tmp.charAt(0) == '\\') {
			
			if (tmp.length() > 1) {
				index++;
				char next = tmp.charAt(index);
				if (next != '\\' && next != '-') {
					
					while (Character.isLetterOrDigit(tmp.charAt(index))) {
						index++;
					}
				}
			}
		}
		while (!Character.isLetter(tmp.charAt(index))) {
			index++;
		}
		if ((index == 0) || (tmp.charAt(index-1) != '|')) {
			tmp.setCharAt(index, Character.toUpperCase(tmp.charAt(index)));
		}
		else if (tmp.charAt(index-1) == '|') {
			tmp.deleteCharAt(index-1);
		}
		_capitalise = false;
		return tmp.toString();
	}
	
	
	protected void newLine() {
		_output.println();
		_currentLinePos = 0;
	}


	public void newParagraph() {
		newLine();
		indent();
		
	}
	enum TypeSetting {
		ADD_TYPESETTING_MARKS, DO_NOTHING, REMOVE_EXISTING_TYPESETTINGMARKS
	};

	private TypeSetting _typeSettingMode = TypeSetting.DO_NOTHING;
	
	public void writeJustifiedOutput(String text, int completionAction, boolean inHtmlRtf, boolean encodeXmlBrackets) {
		if (_typeSettingMode == TypeSetting.REMOVE_EXISTING_TYPESETTINGMARKS) {
			text = RTFUtils.stripFormatting(text);
		}
		else if (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
			// Convert number ranges to use an en dash.
			
			// replace < and > with &lt; and %gt;
			
		}
		
		writeJustifiedText(text, completionAction, inHtmlRtf);
		
	}
	
	private boolean iomcap = false;
	
	private int bufferIndex() {
		return _outputBuffer.length()-1;
	}
	
	private boolean willFitOnLine() {
		return bufferIndex() < Math.abs(_printWidth);
	}
	
	
	/** JSTOUT */
	public void writeJustifiedOutput(String text, int completionAction, boolean inHtmlRtf) {
		// If we are doing chinese output, do that.
		boolean binary = _printWidth < 0;
		int oldBufferPos = bufferIndex();
		
		if (_outputBuffer.length() > 0) {
			if (text.startsWith(BLANK) && lastCharInBuffer() != ' ') {
				_endWordIndex = bufferIndex();
				if (willFitOnLine()) {
					_outputBuffer.append(' ');
					oldBufferPos = bufferIndex();
				}
			}
		}
		int lstr = 0;
		int jin = 0;
		while (jin < text.length() && text.charAt(jin) == ' ') {
			jin++;
		}
		char inchar = ' ';
		int idorepl = 1;
		int nobreak = 0;
		while (jin < text.length()) {
			if (_endWordIndex < 0) {
				indent();
			}
			if ((((inHtmlRtf == false) || (binary == false)) && (text.charAt(jin) == ' ')) == false)  {
				if (_outputBuffer.length()-1 >= Math.abs(_printWidth)) {
					//goto 200;
				}
				char c = text.charAt(jin);
				if (c == '|' && iomcap) {
					_capitalise = false;
					continue;
				}
				boolean ignore = ignore(c);
				
				if ((_capitalise) && (c != '\\') && !inHtmlRtf) {
					c = capitalize(c);
				}
				if ((c == ' ') && (lastCharInBuffer() == ' ')) {
					continue;
				}
				
				if (c == '@') {
					// substitution belongs in the NaturalLanguageTranslator - the only keyword supported in this
					// routine is @name which inserts the taxon name.  Ideally this class doesn't know anything
					// about the data model.
					throw new RuntimeException("Substitution not yet supported!");
				}
				else if (ignore == false && (c == '{')) {
					lstr = -1;
					// Skip {} in text - this looks like a work around for the dodgy RTF groups all through the
					// descriptions in the sample data set.
					jin = jin + 1;
				}
				else if (inHtmlRtf || (_ihtml == false) || (c < 32) || (lookupHtml(c) == null)) {
					lstr = 0;
				}
				else {
					lstr = 0;
					if (c == '\\') {
						_icmd = 0;
					
						// replace the RTF control word with an html equiv.
						//rtf2Html(text, jin);
					}
					else {
						// increment lstr by the size of the html replacement of c.
					}
				}
				if (lstr < 0) {
					// This is the case of an unrecognised RTF control word....
					
				}
				else if (lstr == 0) {
					_outputBuffer.append(c);
					
				}
				else if (_outputBuffer.length()-1 + lstr < Math.abs(_printWidth)) {
					if (c == '\\' || c == '@') {
						
					}
					else {
						// do html substitution.
					}
				}
				if (_endWordIndex <= _currentIndent) {
					_endWordIndex = _outputBuffer.length()-1;
				}
				inchar = c;
				idorepl = 1;
				nobreak = 0;
			}
			jin++;
		}
		_endWordIndex = _outputBuffer.length()-1;
		if (!(_outputBuffer.length()-1 >= Math.abs(_printWidth))) {
			_outputBuffer.append(' ');
		}
		//goto 400;
		
		nobreak = 0;
		if (_endWordIndex <= _currentIndent) {
			// no previous word break on line.
			// set word break at next blank, or when output buffer is full.
			if (jin > text.length()) {
				//goto 500;
			}
			//if ()
		}
	}
	
	private char lastCharInBuffer() {
		if (_outputBuffer.length() == 0) {
			return 0;
		}
		return _outputBuffer.charAt(_outputBuffer.length()-1);
	}
	
	
	private boolean ignore(char ch) {
		boolean ignore = false;
		if (_icmd != 0) {
			ignore = true;
			if ((_icmd == 1) && (ch == '\\')) {
				_icmd = 0;
				ignore = false;
			}
			else if ((_icmd == 1) && (ch == '-')) {
				_icmd = 0;
			} 
			else if (ch == '{') {
				
			}
			else if ((ch == ' ') || (!Character.isDigit(ch) && !Character.isLetter(ch))) {
				_icmd = 0;
			}
			else {
				_icmd =2;
			}
		}
		else if (ch == '\\') {
			_icmd = 1;
			ignore = true;
		}
		return ignore;
	}
	
	private char capitalize(char ch) {
		if (!ignore(ch)) {
			_capitalise = false;
			ch = Character.toUpperCase(ch);
		}
		return ch;
	}
	
	/** This seems to be a lookup table for cp1252->html conversion.*/
	private String lookupHtml(char c) {
		return null;
	}

	public void captialiseNextWord() {
		_capitalise = true;
		
	}

	public void insertPunctuationMark(Word word) {
		
		String punctuationMark = Words.word(word);
		assert punctuationMark.length() == 1;
		
		if (lastCharInBuffer() == punctuationMark.charAt(0)) {
			writeJustifiedOutput(" ", -1, false);
		}
		else {
			writeFromVocabulary(word, -1);
		}
		
	}
	
	public void writeFromVocabulary(Word word, int completionAction) {
		writeJustifiedText(Words.word(word), completionAction, false);
	}

}
