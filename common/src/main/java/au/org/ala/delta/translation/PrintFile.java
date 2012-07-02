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
package au.org.ala.delta.translation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.util.Utils;

/**
 * The PrintFile is a wrapper around an output steam that provides some utility
 * methods for line wrapping and formatting the output.
 */
public class PrintFile {

    private int _printWidth = 80;
    /**
     * The number of lines output on each page of the print file. A value of 0
     * indicates that no paging should be done.
     */
    private int _pageLength = 0;
    /**
     * A count of the number of lines in the current page of the print file content. 
     */
    private int _linesInCurrentPage = 0;
    private PrintStream _output;
    private int _paragraphIndent;
    private int _lineWrapIndent = 0;
    private boolean _useParagraphIndentOnLineWrap = false;
    private boolean _capitalise;

    private StringBuilder _outputBuffer;
    private boolean _indented;
    private boolean _indentOnLineWrap;
    private boolean _softWrap;
    private boolean _newFile;
    private String _newFileHeader;
    private String _fileFooter;
    private boolean _omitNextTrailingSpace = false;
    private char[] _wrapAsGroupChar;
    private boolean _trim;
    private boolean _trimLeadingSpacesOnLineWrap;
    private boolean _outputFixedWidth;

    public PrintFile(final StringBuilder buffer) {

        _output = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                buffer.append((char) b);
            }
        });
        initialise();
    }

    /**
     * Creates a new Printer that will print to the supplied PrintStream.
     * 
     * @param output
     *            the output stream to print to.
     * @param lineWidth
     *            the position at which line wrapping should occur. a lineWidth
     *            of zero means no line wrapping.
     */
    public PrintFile(PrintStream output, int lineWidth) {
        _output = output;
        _printWidth = lineWidth;
        initialise();
    }
    
    /**
     * Creates a new Printer that will print to the supplied PrintStream.
     * 
     * @param output
     *            the output stream to print to.
     * @param lineWidth
     *            the position at which line wrapping should occur. a lineWidth
     *            of zero means no line wrapping.
     */
    public PrintFile(PrintStream output, int lineWidth, int pageLength) {
        this(output, lineWidth);
        _pageLength = pageLength;
    }

    private void initialise() {
        _outputBuffer = new StringBuilder();
        _indented = false;

        _indentOnLineWrap = false;
        _softWrap = false;
        _newFile = true;
        _newFileHeader = "";
        _trim = true;
        _outputFixedWidth = false;
        _trimLeadingSpacesOnLineWrap = false;
    }

    public void setSoftWrap(boolean softWrap) {
        _softWrap = softWrap;
    }

    public void setWrapingGroupChars(char startGroup, char endGroup) {
        _wrapAsGroupChar = new char[] { startGroup, endGroup };
    }

    public void setTrimInput(boolean trim) {
        setTrimInput(trim, false);
    }

    /**
     * Sets the trimming mode for text printed by this object. The default is
     * true.
     * 
     * @param trim
     *            If trim is false, input will not be trimmed before being
     *            output.
     * @param trimLeadingOnLineWrap
     *            If trimLeadingOnLineWrap is true, the leading spaces will be
     *            removed when a line of text is wrapped. This parameter only
     *            has an effect if trim is false.
     */
    public void setTrimInput(boolean trim, boolean trimLeadingOnLineWrap) {
        _trim = trim;
        _trimLeadingSpacesOnLineWrap = trimLeadingOnLineWrap;
    }

    public void setOutputFixedWidth(boolean outputFixedWidth) {
        _outputFixedWidth = outputFixedWidth;
    }

    public void insertTypeSettingMarks(int number) {

    }

    public void close() {
        _output.flush();
        _output.close();
    }

    /**
     * If the indentOnLineWrap property is set to true if a statement is
     * automatically wrapped, its continuation will be indented.
     */
    public void setIndentOnLineWrap(boolean indent) {
        _indentOnLineWrap = indent;
    }

    public void setIndent(int numSpaces) {

        _paragraphIndent = numSpaces;
    }

    public void indent() {
        indent(_paragraphIndent);
    }

    /**
     * Indents the default amount for the current output type. (takes a luntype
     * and a number of spaces)
     */
    public void indent(int indent) {

        if (_indented) {
            return;
        }
        writeFileHeader();
        if (_paragraphIndent <= (Math.abs(_printWidth) - 20)) {
            for (int i = 0; i < _paragraphIndent; i++) {
                _outputBuffer.append(' ');
            }
        }
        _indented = true;
    }

    public void endLine() {
        printBufferLine(false);
    }

    public void writeBlankLines(int numLines, int requiredNumLinesLeftOnPage) {
        writeFileHeader();

        if (_outputBuffer.length() > 0) {
            printBufferLine();
        }
        for (int i = 0; i < numLines; i++) {
            _output.println();
            handleNewPageLine();
        }
    }

    public void printBufferLine() {
        printBufferLine(false);
    }

    public void printBufferLine(boolean indentNewLine) {

        int i = _outputBuffer.length() - 1;
        if (_trim) {
            while (i > 0 && _outputBuffer.charAt(i) == ' ') {
                i--;
            }
        }
        if (_outputBuffer.length() > 0) {

            println(_outputBuffer.substring(0, i + 1));
            _indented = false;
            _outputBuffer = new StringBuilder();

            if (indentNewLine) {
                int lineWrap = _lineWrapIndent;
                if (_useParagraphIndentOnLineWrap) {
                    lineWrap = _paragraphIndent;
                }
                for (int j = 0; j < lineWrap; j++) {
                    _outputBuffer.append(' ');
                }
            }
        }
    }

    protected void println(String text) {

        if (_outputFixedWidth) {
            text = pad(text);
        }

        _output.println(text);
        _output.flush();
        handleNewPageLine();
    }
    
    private void handleNewPageLine() {
        _linesInCurrentPage++;        
        if (_pageLength > 0 && _linesInCurrentPage == _pageLength) {
            _output.println("\f");
            _output.flush();
            _linesInCurrentPage = 0;
        }
    }

    protected String pad(String value) {
        StringBuilder paddedValue = new StringBuilder(value);
        while (paddedValue.length() % _printWidth != 0) {
            paddedValue.append(' ');
        }
        return paddedValue.toString();
    }

    public void writeJustifiedText(String text, int completionAction) {
        writeFileHeader();
        if (_trim) {
            text = text.trim();
        }
        writeJustifiedText(text, completionAction, true);
    }

    private void writeJustifiedText(String text, int completionAction, boolean addSpaceIfRequired) {
        text = doSubstitutions(text);

        if (_capitalise) {
            text = capitaliseFirstWord(text);
        }

        if (needsLineWrap() == false) {
            printBufferLine(_indentOnLineWrap);
        }

        // Insert a space if one is required.
        if (addSpaceIfRequired && !text.startsWith(" ")) {
            insertTrailingSpace();
        }

        _outputBuffer.append(text);

        while (needsLineWrap() == false) {

            int wrappingPos = findWrapPosition();

            String trailingText = _outputBuffer.substring(wrappingPos);
            _outputBuffer.delete(wrappingPos, _outputBuffer.length());
            printBufferLine(_indentOnLineWrap);

            if (_trim) {
                trailingText = trailingText.trim();
            } else if (_trimLeadingSpacesOnLineWrap) {
                trailingText = StringUtils.stripStart(trailingText, null);
            }
            _outputBuffer.append(trailingText);
        }
        complete(completionAction);
    }

    private String doSubstitutions(String text) {
        return KeywordSubstitutions.substitute(text);
    }

    private int findWrapPosition() {
        int wrappingPos = -1;
        int newLinePos = _outputBuffer.indexOf("\n");
        if (newLinePos >= 0 && newLinePos <= _printWidth) {
            wrappingPos = newLinePos;
            _outputBuffer.deleteCharAt(newLinePos);
            if (newLinePos > 0 && _outputBuffer.charAt(newLinePos - 1) == '\r') {
                _outputBuffer.deleteCharAt(newLinePos - 1);
                wrappingPos--;
            }
        } else {
            int numSpaces = numLeadingSpaces(_outputBuffer);
            wrappingPos = findWrappingSpace();
            if (wrappingPos <= numSpaces) {
                if (_softWrap) {
                    wrappingPos = _outputBuffer.indexOf(" ", _printWidth);
                    if (wrappingPos < 0) {
                        wrappingPos = _outputBuffer.length();
                    }
                } else {
                    wrappingPos = _printWidth;
                }
            }
        }

        return wrappingPos;
    }

    private int findWrappingSpace() {
        int wrappingPos;
        if (_wrapAsGroupChar == null) {
            wrappingPos = _outputBuffer.lastIndexOf(" ", _printWidth);
        } else {
            int maxSpace = 0;
            int groupNest = 0;
            int maxGroupStart = 0;
            for (int i = 0; i < _printWidth; i++) {
                if (_wrapAsGroupChar[0] == _wrapAsGroupChar[1]) {
                    if (_outputBuffer.charAt(i) == _wrapAsGroupChar[0]) {
                        groupNest = groupNest == 0 ? 1 : 0;
                    }
                } else {
                    if (_outputBuffer.charAt(i) == _wrapAsGroupChar[0]) {
                        groupNest++;
                        maxGroupStart = i;
                    } else if (_outputBuffer.charAt(i) == _wrapAsGroupChar[1]) {
                        groupNest = Math.max(groupNest - 1, 0);

                    }
                }
                if (_outputBuffer.charAt(i) == ' ') {
                    if (groupNest == 0) {
                        maxSpace = i;
                    }
                }
            }
            if (groupNest > 0 && maxSpace == 0) {
                wrappingPos = maxGroupStart;
            } else if (maxSpace > 0) {
                wrappingPos = maxSpace + 1;
            } else {
                wrappingPos = _printWidth;
            }

        }
        return wrappingPos;
    }

    public void writeTypeSettingMark(String mark) {

        boolean tmpCapitalise = _capitalise;
        _capitalise = false;
        if (!spaceRequired()) {
            _omitNextTrailingSpace = true;
        }
        writeJustifiedText(mark, -1, false);
        _capitalise = tmpCapitalise;
    }

    private void insertTrailingSpace() {
        if (_omitNextTrailingSpace) {
            _omitNextTrailingSpace = false;
            return;
        }
        if (spaceRequired()) {
            _outputBuffer.append(' ');
        }
    }

    private boolean spaceRequired() {
        return (_outputBuffer.length() > 0 && lastCharInBuffer() != ' ');
    }

    private void complete(int completionAction) {
        if ((completionAction == 0) && (needsLineWrap())) {
            _outputBuffer.append(' ');
        } else if (completionAction > 0) {
            writeBlankLines(completionAction, 0);
        }
    }

    /**
     * Returns the number of leading spaces in the supplied text.
     * 
     * @param text
     *            the text to count leading spaces of.
     * @return the number of leading spaces in the supplied text or zero if the
     *         parameter is null.
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
     * Capitalises the first word in the supplied text (which may contain RTF
     * markup) the first letter of the word is preceded by a '|'.
     * 
     * @param text
     *            the text to capitalise.
     * @return the text with the first word capitalised.
     */
    public String capitaliseFirstWord(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        _capitalise = false;
        return Utils.capitaliseFirstWord(text);
    }

    protected void newLine() {
        _output.println();
        handleNewPageLine();
    }

    public void newParagraph() {
        writeFileHeader();
        newLine();
        indent();
    }

    private int bufferIndex() {
        return _outputBuffer.length() - 1;
    }

    private boolean needsLineWrap() {
        if (_printWidth == 0) {
            return true;
        }
        int newLinePos = _outputBuffer.indexOf("\n");
        return bufferIndex() < Math.abs(_printWidth) && (newLinePos == -1);
    }

    private char lastCharInBuffer() {
        if (_outputBuffer.length() == 0) {
            return 0;
        }
        return _outputBuffer.charAt(_outputBuffer.length() - 1);
    }

    public void capitaliseNextWord() {
        _capitalise = true;

    }

    public void insertPunctuationMark(Word word) {

        String punctuationMark = Words.word(word);
        assert punctuationMark.length() == 1;

        if (lastCharInBuffer() != punctuationMark.charAt(0)) {
            writeFromVocabulary(word, -1);
        }
    }

    public void writeFromVocabulary(Word word, int completionAction) {
        writeJustifiedText(Words.word(word), completionAction, false);
    }

    public void outputLine(int indent, String value, int numTrailingBlanks) {
        setIndent(indent);
        outputLine(value, numTrailingBlanks);
    }

    public void outputLine(String value) {
        outputLine(value, 0);
    }

    private void outputLine(String value, int numTrailingBlanks) {
        indent();
        writeJustifiedText(value, -1);
        printBufferLine();
        if (numTrailingBlanks > 0) {
            writeBlankLines(numTrailingBlanks, 0);
        }
    }

    /**
     * Output a pair of strings, separated by multiple instances of a supplied
     * padding character. The padding character is used to ensure that the
     * content fills the print width exactly.
     * 
     * E.g. str1.........................str2
     * 
     * @param str1
     *            The first string
     * @param str2
     *            The second string
     * @param paddingChar
     *            the padding character
     */
    public void outputStringPairWithPaddingCharacter(String str1, String str2, char paddingChar) {
        indent();
        writeJustifiedText(str1, -1);

        int currentLineLength = _outputBuffer.length();
        if (currentLineLength + str2.length() >= _printWidth) {
            _outputBuffer.append(StringUtils.repeat(Character.toString(paddingChar), _printWidth - currentLineLength));
            printBufferLine(_indentOnLineWrap);
            currentLineLength = _outputBuffer.length();
            _outputBuffer.append(StringUtils.repeat(Character.toString(paddingChar), _printWidth - currentLineLength - str2.length()));
            _outputBuffer.append(str2);
            printBufferLine();
        } else {
            _outputBuffer.append(StringUtils.repeat(Character.toString(paddingChar), _printWidth - currentLineLength - str2.length()));
            _outputBuffer.append(str2);
            printBufferLine();
        }
    }

    /**
     * If a line is wrapped during output, the line wrap indent will be applied
     * if the setIndentOnLineWrap(true) method has been invoked.
     * 
     * @param indent
     *            the indent to apply in addition to the paragraph indent if a
     *            line is wrapped.
     */
    public void setLineWrapIndent(int indent) {
        _lineWrapIndent = indent;
    }

    public void setUseParagraphIndentOnLineWrap(boolean b) {
        _useParagraphIndentOnLineWrap = b;
    }

    public void setNewFileHeader(String header) {
        _newFileHeader = header;
    }

    private void writeFileHeader() {
        if (_newFile) {
            if (StringUtils.isNotBlank(_newFileHeader)) {
                writeJustifiedText(_newFileHeader, -1, false);
                _omitNextTrailingSpace = true;
            }
            _newFile = false;
        }
    }

    public void setFileFooter(String footer) {
        _fileFooter = footer;
    }

    private void writeFooter() {
        if (StringUtils.isNotBlank(_fileFooter)) {
            outputLine(_fileFooter);
        }
    }

    public void setPrintWidth(int printWidth) {
        _printWidth = printWidth;
    }
    
    public void setPageLength(int pageLength) {
        _pageLength = pageLength;
    }

    public void setPrintStream(PrintStream stream) {
        // This can occur if we are changing files.

        _output = stream;
    }

    public void closePrintStream() {
        if (_output != null) {
            printBufferLine();
            writeFooter();
            IOUtils.closeQuietly(_output);
            _output = null;
        }
    }

}
