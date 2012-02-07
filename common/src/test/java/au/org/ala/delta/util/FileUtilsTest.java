package au.org.ala.delta.util;

import java.io.File;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {
	
	public void testFindFileIgnoreCase() {
		File f = FileUtils.findFileIgnoreCase("c:\\zz\\JSON.JSON");
	}

}
