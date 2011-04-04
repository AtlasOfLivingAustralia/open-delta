package au.org.ala.delta.translation;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.NaturalLanguageTranslator.TypeSetting;
import au.org.ala.delta.util.Utils;

/**
 * The TypeSetter is responsible for formatting output produced by the NaturalLanguageTranslator.
 */
public class TypeSetter {

	private static final String BLANK = " ";
	private DeltaContext _context;
	private int _printWidth = 80;
	private PrintStream _output;
	private int _currentIndent;
	private int _currentLinePos;
	private int _icmd;
	private int _icap;

	private StringBuilder _outputBuffer;
	private int _endWordIndex;
	
	public TypeSetter() throws Exception {
		_output = new PrintStream(new File("c:\\temp\\test-confor-output"));

	}

	public void insertTypeSettingMarks(int number) {

	}

	public void indent(int numSpaces) {

		if (numSpaces > _printWidth - 20) {
			return;
		}

		else {
			_currentIndent = numSpaces;
			indent();
		}
	}

	/**
	 * Indents the default amount for the current output type. (takes a luntype
	 * and a number of spaces)
	 */
	public void indent() {
		if (_currentIndent <= (Math.abs(_printWidth) - 20) ) {
			for (int i = 0; i < _currentIndent; i++) {
				print(BLANK);
			}
		}
		_endWordIndex = 0;
	}

	public void writeBlankLines(int numLines, int requiredNumLinesLeftOnPage) {
		for (int i = 0; i < numLines; i++) {
			_output.println();
		}
	}

	public void writeSentence(String sentence, boolean includeFullStop) {
		
		if (sentence.startsWith("|")) {
			sentence = sentence.substring(1);
		}
		else {
			sentence = WordUtils.capitalize(sentence);
		}

		writeText(sentence);

		if (includeFullStop) {
			print(".");
		}
		newLine();
	}
	
	public void writeSentence(String sentence) {
		writeSentence(sentence, true);
	}

	public void writeText(String word) {
		if (_currentLinePos + word.length() > _printWidth) {
			word = WordUtils.wrap(word, _printWidth-_currentLinePos);
			
			String words[] = word.split(SystemUtils.LINE_SEPARATOR);
			
			if (words.length == 1) {
				word = words[0];
			}
			else {
				word = StringUtils.join(Arrays.copyOfRange(words, 1, words.length), ' ');
				print(words[0]);	
			}
			
			newLine();
		
			writeText(word);
		}
		else {
			if (_currentLinePos != _currentIndent) {
				print(BLANK);
			}
			print(word);
		}		
	}
	
	protected void print(String text) {
		_output.print(text);
		_currentLinePos += text.length();
	}
	
	protected void newLine() {
		_output.println();
		_currentLinePos = 0;
	}

	protected void completePreviousWord() {
		_output.println();
	}

	public void writeInt(int i) {
		_output.print(i);
	}

	public void write(String text) {
		_output.print(text);
	}

	public void newParagraph() {
		newLine();
		indent(8);
		
	}
	
	public void writeJustifiedOutput(String text, boolean inHtml, boolean encodeXmlBrackets, TypeSetting typeSettingMode) {
		if (typeSettingMode == TypeSetting.REMOVE_EXISTING_TYPESETTINGMARKS) {
			text = RTFUtils.stripFormatting(text);
		}
		else if (typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
			// Convert number ranges to use an en dash.
			
			// replace < and > with &lt; and %gt;
			
		}
		
	}
	
	private boolean iomcap = false;
	
	/** JSTOUT */
	public void writeJustifiedOutput(String text, int completionAction, boolean inHtmlRtf) {
		// If we are doing chinese output, do that.
		boolean binary = _printWidth < 0;
		int oldBufferPos = _outputBuffer.length()-1;
		
		if (_outputBuffer.length() > 0) {
			if (!text.startsWith(BLANK) && _outputBuffer.charAt(_outputBuffer.length()-1) != ' ') {
				_endWordIndex = _outputBuffer.length()-1;
				if (_endWordIndex < Math.abs(_printWidth)) {
					_outputBuffer.append(' ');
					oldBufferPos = _outputBuffer.length()-1;
				}
			}
		}
		
		int jin = 0;
		while (jin < text.length() && text.charAt(jin) == ' ') {
			jin++;
		}
		if (_endWordIndex < 0) {
			indent();
		}
		if ((((inHtmlRtf == false) || (binary == false)) && (text.charAt(jin) == ' ')) == false)  {
			if (_outputBuffer.length()-1 >= Math.abs(_printWidth)) {
				//goto 200;
			}
			char c = text.charAt(jin);
			if (c == '|' && iomcap) {
				_icap = 0;
				//goto 150;
			}
			if ((_icap != 0) && (c != '\\') && !inHtmlRtf) {
				c = capitalize(c);
			}
			if ((c == ' ') && (lastCharInBuffer() == ' ')) {
				//goto 150;
			}
			
			if (c == '@') {
				
			}
		}
		
		
	}
	
	private char lastCharInBuffer() {
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
			_icap = 0;
			ch = Character.toUpperCase(ch);
		}
		return ch;
	}
}
