package au.org.ala.delta.rtf;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class RTFReaderTests extends TestCase {

	public void testReader1() throws IOException {
		FileInputStream fis = new FileInputStream("c:/zz/test2.rtf");
		
		RTFReader reader = new RTFReader(fis);
		reader.parse();
	}

}
