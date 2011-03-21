package au.org.ala.delta.translation;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;

import au.org.ala.delta.DeltaContext;

/**
 * The TypeSetter is responsible for formatting output produced by the NaturalLanguageTranslator.
 */
public class TypeSetter {

	private static final String BLANK = " ";
	private DeltaContext _context;
	private int _lineWidth = 80;
	private PrintStream _output;
	private int _currentIndent;
	private int _currentLinePos;

	public TypeSetter() throws Exception {
		_output = new PrintStream(new File("c:\\temp\\test-confor-output"));

	}

	public void insertTypeSettingMarks(int number) {

	}

	public void indent(int numSpaces) {

		if (numSpaces > _lineWidth - 20) {
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
		for (int i = 0; i < _currentIndent; i++) {
			print(BLANK);
		}
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
		if (_currentLinePos + word.length() > _lineWidth) {
			word = WordUtils.wrap(word, _lineWidth-_currentLinePos);
			
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
}
