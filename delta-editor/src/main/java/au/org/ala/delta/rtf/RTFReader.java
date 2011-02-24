package au.org.ala.delta.rtf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Stack;

public class RTFReader {

	private int _cGroup = 0;
	private PushbackInputStream _stream;
	private Stack<ParserState> _stateStack = new Stack<ParserState>();
	private ParserState _parserState;
	private long _cbBin = 0;
	
	public RTFReader(InputStream stream) {
		_stream = new PushbackInputStream(stream);
	}
	
	public RTFReader(String rtf) {
		_stream = new PushbackInputStream(new ByteArrayInputStream(rtf.getBytes()));
	}

	public void parse() throws IOException {
		int cNibble = 2;
		int intCh = 0;	
		short b = 0;
		_parserState = new ParserState();
		
		while ((intCh = _stream.read()) >= 0) {
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
	}
	
	private void parseRtfKeyword() throws IOException {
		int ch;
		boolean isNeg = false;
		boolean hasParam = false;
		int param = 0;
		
		if ((ch = _stream.read()) < 0) {
			return;
		}
		
		if (!Character.isLetter((char) ch)) {
			// Control symbol...
			translateKeyword("" + (char) ch, 0, false);
			return;
		}
		
		StringBuilder keyword = new StringBuilder();
		for (;Character.isLetter((char) ch) && ch >= 0; ch = _stream.read()) {
			keyword.append((char) ch);
		}
		
		if ((char) ch == '-') {
			isNeg = true;
			if ((ch = _stream.read()) < 0) {
				return;
			}
		}
		
		if (Character.isDigit((char) ch)) {
			StringBuilder strParam = new StringBuilder();
			hasParam = true;
			for (;Character.isDigit((char) ch); ch = _stream.read()) {
				strParam.append((char) ch);
			}
			param = Integer.parseInt(strParam.toString());
			if (isNeg) {
				param = -param;
			}			
		}
		
		if (ch != ' ') {
			_stream.unread(ch);
		}
		
		translateKeyword(keyword.toString(), param, hasParam);		
	}
	
	private void translateKeyword(String keyword, int param, boolean hasParam) {
		System.err.println("translate keyword: " + keyword);
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
			default:
				// TODO handle other destination types
		}
		
	}
	
	private void printChar(char ch) {
		System.out.print(ch);
	}
	
	private void pushParserState() {
		// Save the current state,
		_stateStack.push(_parserState);
		// and create a new one based on this one
		_parserState = new ParserState(_parserState);
		// execpt that RIS is reset
		_parserState.ris = ParserInternalState.Normal;
		// increment group depth
		_cGroup++;
	}
	
	private void popParserState() {
		if (_stateStack.size() == 0) {
			throw new RuntimeException("Group Stack underflow!");
		}
		
		ParserState prevState = _stateStack.pop();
		if (_parserState.rds != prevState.rds) {
			endGroupAction(_parserState.rds);
		}
		
		_parserState = prevState;
		_cGroup--;		
	}

	private void endGroupAction(DestinationState rds) {
	}

}
