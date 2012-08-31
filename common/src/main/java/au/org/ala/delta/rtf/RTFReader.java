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
package au.org.ala.delta.rtf;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RTFReader {

	private int _position = 0;
    private int _cGroup = 0;
    private PushbackReader _stream;
    private Stack<ParserState> _stateStack = new Stack<ParserState>();
    private ParserState _parserState;
    private long _cbBin = 0;
    private StringBuilder _headerGroupBuffer = new StringBuilder();
    private String _headerGroupKeyword = null;

    private RTFHandler _handler;

    public RTFReader(Reader stream, RTFHandler handler) {
        _stream = new PushbackReader(stream);
        _handler = handler;
    }

    public RTFReader(String rtf, RTFHandler handler) {
        _stream = new PushbackReader(new StringReader(rtf));
        _handler = handler;
    }
    
    public int position() {
    	return _position;
    }

    public void parse() throws IOException {
        int cNibble = 2;
        int intCh = 0;
        short b = 0;
        _parserState = new ParserState();

        if (_handler != null) {
            _handler.startParse();
        }

        while ((intCh = read()) >= 0) {
            char ch = (char) intCh;
            if (_cGroup < 0) {
                throw new RuntimeException("Group stack underflow exception");
            }

            if (_parserState.ris == ParserInternalState.Binary) {
                parseChar(ch);
            } else {
                switch (ch) {
                case '{':
                    pushParserState();
                    break;
                case '}':
                    popParserState();
                    break;
                case '\\':
                    parseRtfKeyword();
                    break;
                case '\n':
                case '\r':
                    break;
                default:
                    if (_parserState.ris == ParserInternalState.Normal) {
                        parseChar(ch);
                    } else {
                        if (_parserState.ris != ParserInternalState.Hex) {
                            throw new RuntimeException("Invalid parser state - expected Hex");
                        }

                        b = (short) (b << 4);
                        if (Character.isDigit(ch)) {
                            b += ch - '0';
                        } else {
                            if (Character.isLowerCase(ch)) {
                                if (ch < 'a' || ch > 'f') {
                                    throw new RuntimeException("Invalid Hex char: " + ch);
                                }
                                b += ch - 'a';
                            } else {
                                if (ch < 'A' || ch > 'F') {
                                    throw new RuntimeException("Invalid Hex char: " + ch);
                                }
                                b += ch - 'A';
                            }
                        }
                        cNibble--;
                        if (cNibble == 0) {
                            parseChar((char) b);
                            cNibble = 2;
                            b = 0;
                            _parserState.ris = ParserInternalState.Normal;
                        }
                    }
                    break;
                }
            }
        }
        if (_cGroup < 0) {
            throw new RuntimeException("Group Stack Underflow");
        }

        if (_cGroup > 0) {
            throw new RuntimeException("Unmatched '{'");
        }

        if (_handler != null) {
            _handler.endParse();
        }

    }

    private void parseRtfKeyword() throws IOException {
        int ch;
        boolean isNeg = false;
        boolean hasParam = false;
        int param = 0;

        if ((ch = read()) < 0) {
            return;
        }
        
        // Escaped backslash, open and close brace. Simply output the
        // character
        if (ch == '\\' || ch == '{' || ch == '}') {
            printChar((char) ch);
            return;
        }

        if (!Character.isLetter((char) ch)) {
            // Control symbol...
            translateKeyword("" + (char) ch, 0, false);
            return;
        }

        StringBuilder keyword = new StringBuilder();
        for (; Character.isLetter((char) ch) && ch >= 0; ch = read()) {
            keyword.append((char) ch);
        }

        if ((char) ch == '-') {
            isNeg = true;
            if ((ch = read()) < 0) {
                return;
            }
        }

        if (Character.isDigit((char) ch)) {
            StringBuilder strParam = new StringBuilder();
            hasParam = true;
            for (; Character.isDigit((char) ch); ch = read()) {
                strParam.append((char) ch);
            }
            param = Integer.parseInt(strParam.toString());
            if (isNeg) {
                param = -param;
            }
        }

        if (ch != ' ' && ch >= 0) {
            unread(ch);
        }

        translateKeyword(keyword.toString(), param, hasParam);
    }

    /**
     * This method is to allow SpecialKeyword to read data from the input
     * stream.
     * 
     * @return the next value from the stream being parsed.
     * @throws IOException
     *             if there is an error reading from the stream.
     */
    int read() throws IOException {
    	_position++;
        return _stream.read();
    }
    
    int readNextLiteral() throws IOException {
    	int ch = read();
    	if (ch == '\\') {
    		int next = read();
    		if (next == '\'') {					 	// \'XX where XX is a hex digit representing an ANSI (cp1252) character
    			char[] hex = new char[2];
    			hex[0] = (char) read();
    			hex[1] = (char) read();    			
    			ch = Integer.parseInt(new String(hex), 16);
    		} else if ("{}\\".indexOf(next) >= 0) {	// An escaped control symbol (one of {}\)
    			return next;
    		} else {
    			// The next literal could not be determined - its a control word that doesn't yield a single character. 
    			// For now just return space. This may indicate an error in the RTF, or that
    			// we are not correctly handling all literal types
    			unread(next);
    			unread(ch);
    			return ' ';
    		}
    	}
    	return ch;
    }
    
    /**
     * This method is to allow SpecialKeyword to unread data from the input
     * stream.
     * 
     * @throws IOException
     *             if there is an error reading from the stream.
     */
    void unread(int ch) throws IOException {
    	_stream.unread(ch);
        _position--;
    }
    

    /**
     * This method is to allow SpecialKeyword access to the parser state.
     * 
     * @return the current state of the reader.
     */
    ParserState currentState() {
        return _parserState;
    }

    private void translateKeyword(String keyword, int param, boolean hasParam) throws IOException {

        if (Keyword.KEYWORDS.containsKey(keyword)) {
            Keyword kwd = Keyword.KEYWORDS.get(keyword);
            switch (kwd.getKeywordType()) {
            case Character:
                parseChar(((CharacterKeyword) kwd).getOutputChar());
                break;
            case Destination:
                _parserState.rds = ((DestinationKeyword) kwd).getDestinationState();
                if (_parserState.rds == DestinationState.Header) {
                    _headerGroupBuffer = new StringBuilder();
                    _headerGroupKeyword = keyword;
                }
                break;
            case Attribute:
                // Attributes are not applicable with we are currently
                // processing a header group.
                // In this situation simply append to the header group buffer.
                if (_parserState.rds == DestinationState.Header) {
                    _headerGroupBuffer.append("\\").append(keyword);
                    if (hasParam) {
                        _headerGroupBuffer.append(param);
                    }
                    _headerGroupBuffer.append(" ");
                } else {
                    if (kwd instanceof CharacterAttributeKeyword) {
                        CharacterAttributeKeyword charAttrKwd = (CharacterAttributeKeyword) kwd;

                        if (hasParam) {
                            _parserState.CharacterAttributes.set(charAttrKwd.getType(), param);
                        } else {
                            _parserState.CharacterAttributes.set(charAttrKwd.getType(), charAttrKwd.getDefaultValue());
                        }

                        AttributeValue val = new AttributeValue(charAttrKwd.getKeyword(), hasParam, param);
                        if (_handler != null) {
                            List<AttributeValue> values = new ArrayList<AttributeValue>();
                            values.add(val);
                            _handler.onCharacterAttributeChange(values);
                        }
                    } else {
                        // keyword must be a paragraph attribute keyword
                        ParagraphAttributeKeyword paraAttrKwd = (ParagraphAttributeKeyword) kwd;

                        if (hasParam) {
                            _parserState.ParagraphAttributes.set(paraAttrKwd.getType(), param);
                        } else {
                            _parserState.ParagraphAttributes.set(paraAttrKwd.getType(), paraAttrKwd.getDefaultValue());
                        }

                        AttributeValue val = new AttributeValue(paraAttrKwd.getKeyword(), hasParam, param);
                        if (_handler != null) {
                            List<AttributeValue> values = new ArrayList<AttributeValue>();
                            values.add(val);
                            _handler.onParagraphAttributeChange(values);
                        }
                    }
                }
                break;
            case Special:
                char[] output = ((SpecialKeyword) kwd).process(param, this);
                for (char ch : output) {
                    parseChar(ch);
                }
                break;
            default:
                break;
            }
        } else {
            if (_parserState.rds == DestinationState.Header) {
                _headerGroupBuffer.append("\\").append(keyword);
                if (hasParam) {
                    _headerGroupBuffer.append(param);
                }
                _headerGroupBuffer.append(" ");
            } else {
                if (keyword.equals("pard")) {
                    startParagraphAction();
                } else if (keyword.equals("par")) {
                    endParagraphAction();
                }

                if (_handler != null) {
                    _handler.onKeyword(keyword, hasParam, param);
                }
            }
        }

    }

    private void parseChar(char ch) {
        if (_parserState.ris == ParserInternalState.Binary && --_cbBin < 0) {
            _parserState.ris = ParserInternalState.Normal;
        }

        switch (_parserState.rds) {
        case Skip:
            return;
        case Normal:
            printChar(ch);
            return;
        case Header:
            _headerGroupBuffer.append(ch);
            break;
        default:
            // TODO handle other destination types
        }

    }

    private void printChar(char ch) {
        if (_handler != null) {
            _handler.onTextCharacter(ch);
        }
    }

    private void pushParserState() {
        // Save the current state,
        _stateStack.push(_parserState);
        // and create a new one based on this one
        _parserState = new ParserState(_parserState);
        // except that RIS is reset
        _parserState.ris = ParserInternalState.Normal;
        // increment group depth
        _cGroup++;
        if (_parserState.rds == DestinationState.Header) {
            _headerGroupBuffer.append("{");
        }
    }

    private void popParserState() {
        if (_stateStack.size() == 0) {
            throw new RuntimeException("Group Stack underflow!");
        }

        ParserState prevState = _stateStack.pop();
        if (_parserState.rds == DestinationState.Normal || _parserState.rds == DestinationState.Header) {
            endGroupAction(_parserState, prevState);
        }

        _parserState = prevState;
        _cGroup--;

        if (_parserState.rds == DestinationState.Header) {
            _headerGroupBuffer.append("}");
        }

    }

    private void endGroupAction(ParserState currentState, ParserState previousState) {
        switch (currentState.rds) {
        case Header:
            if (previousState.rds == DestinationState.Normal) {
                emitHeaderGroup(_headerGroupKeyword, _headerGroupBuffer.toString());
                _headerGroupBuffer = new StringBuilder();
                _headerGroupBuffer = null;
            }
            break;
        default:
            List<AttributeValue> changes = new ArrayList<AttributeValue>();

            for (CharacterAttributeType attrType : CharacterAttributeType.values()) {
                int currentVal = currentState.CharacterAttributes.get(attrType);
                int previousVal = previousState.CharacterAttributes.get(attrType);
                if (previousVal != currentVal) {
                    AttributeKeyword attrKeyword = Keyword.findCharacterAttributeKeyword(attrType);
                    if (attrKeyword != null) {
                        changes.add(new AttributeValue(attrKeyword.getKeyword(), true, previousVal));
                    }
                }
            }

            if (_handler != null && changes.size() > 0) {
                _handler.onCharacterAttributeChange(changes);
            }
            break;
        }
    }

    private void emitHeaderGroup(String keyword, String content) {
        if (_handler != null) {
            _handler.onHeaderGroup(keyword, content);
        }
    }

    // pard
    private void startParagraphAction() {
        // reset paragraph attributes
        _parserState.ParagraphAttributes = new ParagraphAttributes();
        _parserState.ParagraphAttributes.set(ParagraphAttributeType.LeftJustify, 0);
        _parserState.ParagraphAttributes.set(ParagraphAttributeType.FirstLineIndent, 0);
        _parserState.ParagraphAttributes.set(ParagraphAttributeType.LeftBlockIndent, 0);
        _parserState.ParagraphAttributes.set(ParagraphAttributeType.RightBlockIndent, 0);

        if (_handler != null) {
            List<AttributeValue> changes = new ArrayList<AttributeValue>();
            changes.add(new AttributeValue(Keyword.findParagraphAttributeKeyword(ParagraphAttributeType.LeftJustify).getKeyword(), false, 0));
            changes.add(new AttributeValue(Keyword.findParagraphAttributeKeyword(ParagraphAttributeType.FirstLineIndent).getKeyword(), true, 0));
            changes.add(new AttributeValue(Keyword.findParagraphAttributeKeyword(ParagraphAttributeType.LeftBlockIndent).getKeyword(), true, 0));
            changes.add(new AttributeValue(Keyword.findParagraphAttributeKeyword(ParagraphAttributeType.RightBlockIndent).getKeyword(), true, 0));
            _handler.onParagraphAttributeChange(changes);
        }

        if (_handler != null) {
            _handler.startParagraph();
        }
    }

    // par
    private void endParagraphAction() {
        if (_handler != null) {
            _handler.endParagraph();
        }
    }

}
