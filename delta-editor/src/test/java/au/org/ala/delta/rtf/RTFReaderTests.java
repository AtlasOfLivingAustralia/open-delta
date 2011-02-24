package au.org.ala.delta.rtf;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

public class RTFReaderTests extends TestCase {

	public void testReader1() throws IOException {
		URL url = getClass().getResource("/rtf/test1.rtf");
		RTFReader reader = new RTFReader(url.openStream());
		reader.parse();
	}

}
