package au.org.ala.delta.rtf;

import java.io.IOException;

public class CodePageKeyword extends SpecialKeyword {
	
	public CodePageKeyword() {
		super("'");
	}

	@Override
	public char[] process(int param, RTFReader reader) throws IOException {
		char[] hex = new char[2];
		hex[0] = (char) reader.read();
		hex[1] = (char) reader.read();
		
		int value = Integer.parseInt(new String(hex), 16);
		return new char[]  { (char) value };		
	}

}
