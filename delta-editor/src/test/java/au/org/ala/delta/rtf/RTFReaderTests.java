package au.org.ala.delta.rtf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.TestCase;

public class RTFReaderTests extends TestCase {

	public void testReader1() throws IOException {
		String rtf = getFileAsString("/rtf/test1.rtf");
		String stripped = RTFUtils.stripFormatting(rtf);
		System.out.println(stripped);
	}

	public void testReader2() throws IOException {
		String rtf = getFileAsString("/rtf/test2.rtf");
		String stripped = RTFUtils.stripUnrecognizedRTF(rtf);
		System.out.println(stripped);
	}
	
	public void testReader3() throws IOException {
		String rtf = "{\\rtf\\ansi\\deff0{\\fonttbl{\\f0\\froman Tms Rmn;}}\\pard\\plain \\fs20 \\super This is plain text. \\super0\\par{\\b\\i This is bold italic}}";
		String stripped = RTFUtils.stripUnrecognizedRTF(rtf);
		System.out.println(stripped);
		
		stripped = RTFUtils.stripFormatting(rtf);
		System.out.println(stripped);			
	}

	private String getFileAsString(String resource) throws IOException {
		URL url = getClass().getResource(resource);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		return stringBuilder.toString();
	}

}
