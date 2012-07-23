package au.org.ala.delta.util;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

	public void testFindFileIgnoreCase() {
		FileUtils.findFileIgnoreCase("c:\\zz\\JSON.JSON");
	}
	
//	public void testMakeRelativeTo1() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("c:\\testfolder\\test1"));
//		String expected = "test1";
//		assertEquals(expected, actual);
//	}
//	
//	public void testMakeRelativeTo2() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("c:\\otherFolder\\test1"));
//		String expected = "..\\otherFolder\\test1";
//		assertEquals(expected, actual);
//	}
//	
//	public void testMakeRelativeTo3() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("c:\\testfolder"));
//		String expected = ".";
//		assertEquals(expected, actual);
//	}
//	
//	public void testMakeRelativeTo4() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("d:\\testfolder"));
//		String expected = null;
//		assertEquals(expected, actual);
//	}
	
}
