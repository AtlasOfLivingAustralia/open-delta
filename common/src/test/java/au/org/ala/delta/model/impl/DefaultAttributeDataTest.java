package au.org.ala.delta.model.impl;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.math.Range;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.NumericRange;

/**
 * Tests the DefaultAttributeData class.
 */
public class DefaultAttributeDataTest extends TestCase {

	private MutableDeltaDataSet _dataSet;
	
	@Before
	public void setUp() {
		_dataSet = new DefaultDataSetFactory().createDataSet("test");
		_dataSet.addCharacter(CharacterType.IntegerNumeric);
	}
	
	/**
	 * Tests a single range with all possible values specified.
	 */
	@Test
	public void testGetNumericValue() {	
		List<NumericRange> values = rangesFor("(1-)2-3-4(-5)");
		assertEquals(1, values.size());
		
		NumericRange range = values.get(0);
		checkRange(range, 1, 5, 3, 2, 4);
	}
	
	/**
	 * Tests multiple ranges with only a single value specified in each.
	 */
	@Test
	public void testMultipleRanges() {	
		List<NumericRange> values = rangesFor("1/2&3");
		assertEquals(3, values.size());
		
		int[] expected = {1,2,3};
		for (int i=0; i<expected.length; i++) {
			NumericRange range = values.get(i);
			checkRange(range, expected[i], expected[i]);
			assertNull(range.getMiddle());
		}
	}
	
	@Test
	public void testNoExtremes() {
		List<NumericRange> values = rangesFor("1-2-3");
		assertEquals(1, values.size());
		NumericRange range = values.get(0);
		checkRange(range, 1, 3);
		assertFalse(range.hasExtremeHigh());
		assertFalse(range.hasExtremeLow());
		assertEquals(2, range.getMiddle().intValue());
			
	}
	
	private void checkRange(NumericRange range, int extremeMin, int extremeMax, int middle, int min, int max) {
		assertEquals(extremeMin, range.getExtremeLow().intValue());
		assertEquals(extremeMax, range.getExtremeHigh().intValue());
		assertEquals(middle, range.getMiddle().intValue());
		Range normalRange = range.getNormalRange();
		assertEquals(min, normalRange.getMinimumInteger());
		assertEquals(max, normalRange.getMaximumInteger());
	}
	
	private void checkRange(NumericRange range, int min, int max) {
		assertNull(range.getExtremeLow());
		assertNull(range.getExtremeHigh());
		
		Range normalRange = range.getNormalRange();
		assertEquals(min, normalRange.getMinimumInteger());
		assertEquals(max, normalRange.getMaximumInteger());
	}
	
	
	private List<NumericRange> rangesFor(String attribute) {
		DefaultAttributeData data = new DefaultAttributeData(_dataSet.getCharacter(1));
		data.setValueFromString(attribute);
		
		return data.getNumericValue();
	}
	
	
	
}
