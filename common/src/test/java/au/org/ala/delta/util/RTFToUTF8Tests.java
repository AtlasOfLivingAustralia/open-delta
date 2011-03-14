package au.org.ala.delta.util;

import junit.framework.TestCase;

public class RTFToUTF8Tests extends TestCase {

	public void test1() {
		String rtf = "present, at least as an acute\\'a9angled fold";
		byte[] expected = new byte[] { 112, 114, 101, 115, 101, 110, 116, 44,
				32, 97, 116, 32, 108, 101, 97, 115, 116, 32, 97, 115, 32, 97,
				110, 32, 97, 99, 117, 116, 101, -62, -87, 97, 110, 103, 108,
				101, 100, 32, 102, 111, 108, 100 };
		byte[] actual = Utils.RTFToUTF8(rtf);

		assertEquals(expected.length, actual.length);

		for (int i = 0; i < expected.length; ++i) {
			assertEquals(expected[i], actual[i]);
		}
	}

}