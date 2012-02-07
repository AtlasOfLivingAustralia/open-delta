package au.org.ala.delta.util;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

	public void testFindFileIgnoreCase() {
		FileUtils.findFileIgnoreCase("c:\\zz\\JSON.JSON");
	}

}
