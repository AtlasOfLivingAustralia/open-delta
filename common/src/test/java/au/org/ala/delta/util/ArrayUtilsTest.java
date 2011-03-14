package au.org.ala.delta.util;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Tests the ArrayUtils class.
 */
public class ArrayUtilsTest {

	/**
	 * Tests the deleteRange method.
	 */
	@Test public void testDeleteRange() throws Exception {

		String source = "Test Delete Range";
		
		byte[] result = ArrayUtils.deleteRange(source.getBytes("UTF-8"), 5, 12);
		
		String resultStr = new String(result, "UTF-8");
		
		assertEquals("Test Range", resultStr);
	}
	
	/**
	 * Tests the deleteRange method.
	 */
	@Test public void testInsert() throws Exception {

		byte[] source = "Test Range".getBytes("UTF-8");
		byte[] toInsert = "Insert ".getBytes("UTF-8");
		
		byte[] result = ArrayUtils.insert(source, 5, toInsert);
		
		String resultStr = new String(result, "UTF-8");
		
		assertEquals("Test Insert Range", resultStr);
	}

}
