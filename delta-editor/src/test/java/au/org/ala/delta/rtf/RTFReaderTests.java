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
	
	
	/**
	 * Tests the RTFReader can correctly handle escaped unicode code points of the form \\u<code point>.
	 */
	public void testUnicodeConversion() throws IOException {
		String rtf = "{\\rtf\\ansi\\deff0{\\fonttbl{\\f0\\froman Tms Rmn;}}\\pard\\plain This is \\u2222? text.}";
		String stripped = RTFUtils.stripFormatting(rtf);
		// 08AE is 2222 expressed in Hex.
		String expectedResult = "This is \u08AE text.";
		
		System.out.println(Integer.toHexString(-33000));
		System.out.println(Short.MAX_VALUE);
		System.out.println(Integer.toHexString(Short.MAX_VALUE));
		
		
		assertEquals(expectedResult, stripped);
	}
	
	/**
	 * Tests the RTFReader can correctly handle code points expressed as negative numbers (the RTF spec
	 * expects that parameters to keywords are 16 bit signed integers.  Not sure what happens to code points 
	 * above FFFF though, probably not supported by RTF).
	 */
	public void testUnicodeConversionNegativeParamater() throws IOException {
		String rtf = "{\\rtf\\ansi\\deff0{\\fonttbl{\\f0\\froman Tms Rmn;}}\\pard\\plain This is \\u-1? text.}";
		String stripped = RTFUtils.stripFormatting(rtf);
		// in 16 bit 2s complement, -1 is 0xFFFF.
		String expectedResult = "This is \uFFFF text.";
		
		rtf = "{\\rtf\\ansi\\deff0{\\fonttbl{\\f0\\froman Tms Rmn;}}\\pard\\plain This is \\u-32768? text.}";
		stripped = RTFUtils.stripFormatting(rtf);
		// in 16 bit 2s complement Short.MIN_VALUE is 0x8000
		expectedResult = "This is \u8000 text.";
		
		assertEquals(expectedResult, stripped);
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
