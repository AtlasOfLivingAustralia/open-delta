/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.slotfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.CharType;
import au.org.ala.delta.editor.slotfile.ChunkType;
import au.org.ala.delta.editor.slotfile.DeltaNumber;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;

/**
 * Tests the Attribute class.
 * 
 */
public class AttributeTest extends TestCase {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/** A mock of the VOCharBaseDesc we use to assist with our tests */
	private VOCharBaseDesc fakeVO;
	
	/**
	 * Tests that a single integer numeric attribute is parsed correctly.
	 */
	@Test public void testParseIntegerChar() {
		fakeVO = setupExpectationsForIntegerCharacter();
		String value = "12";
		testNumber(value, 12f, 0);
	}
	
	/**
	 * Tests that a single real numeric attribute is parsed correctly.
	 */
	@Test public void testParseRealChar() {
		fakeVO = setupExpectationsForRealCharacter();
		String value = "12.444";
		testNumber(value, 12.444f, 3);
	}
	
	/**
	 * Tests that a real numeric attribute of the form: 
	 * <value><comment> / <comment><value> is parsed correctly.
	 */
	@Test public void testParseRealCharWithComments() {
		fakeVO = setupExpectationsForRealCharacter();
		String value = "12.444<or thereabouts>";
		
		Attribute attribute = new Attribute(value, fakeVO);
		
		assertEquals(2, attribute.getNChunks());
		
		byte[] data = attribute.getData();
		
		testNumberChunkCorrect(data, 0, 12.444f, 3);
		
		value = "<about>3.3333";
		attribute = new Attribute(value, fakeVO);
		assertEquals(2, attribute.getNChunks());
		
		data = attribute.getData();
		int offset = 0;
		offset = testShortTextChunkCorrect(data, offset, "about");
		
		testNumberChunkCorrect(data, offset, 3.333f, 4);
	}

	/**
	 * Tests that a numeric attribute of the form: 
	 * <low value>-<high value> is parsed correctly.
	 */
	@Test public void testNumberRange() {
		fakeVO = setupExpectationsForIntegerCharacter();
		String value = "100-110";
		Attribute attribute = new Attribute(value, fakeVO);
			
		int offset = 0;
		byte[] data = attribute.getData();

		assertEquals(3, attribute.getNChunks());
		
		// Should be NUMBER, TO, NUMBER
		// First number - 100
		offset = testNumberChunkCorrect(data, offset, 100f, 0);
		
		// The "-" delimiter (ChunkType.CHUNK_TO)
		assertEquals(ChunkType.CHUNK_TO, data[offset]);
		offset++;
		
		// Second number - 110
		assertEquals(3, data[offset]);
		offset = testNumberChunkCorrect(data, offset, 110f, 0);
		
	}
	
	/**
	 * Tests that a numeric attribute including extremes and a 3 range value is parsed correctly.
	 */
	@Test public void testNumberRangeWithExtremes() {
		fakeVO = setupExpectationsForIntegerCharacter();
		String value = "(1-)2-3-4(-5)";
		Attribute attribute = new Attribute(value, fakeVO);
		
		int offset = 0;
		byte[] data = attribute.getData();
		
		assertEquals(7, attribute.getNChunks());
		
		// Should be Extreme Low, number, to, number, to, number, extreme high
		offset = testNumericChunkCorrect(data, offset, ChunkType.CHUNK_EXLO_NUMBER, 1f, 0);
		offset = testNumberChunkCorrect(data, offset, 2f, 0);
		// The "-" delimiter (ChunkType.CHUNK_TO)
		assertEquals(ChunkType.CHUNK_TO, data[offset++]);
		offset = testNumberChunkCorrect(data, offset, 3f, 0);
		assertEquals(ChunkType.CHUNK_TO, data[offset++]);
		offset = testNumberChunkCorrect(data, offset, 4f, 0);
		offset = testNumericChunkCorrect(data, offset, ChunkType.CHUNK_EXHI_NUMBER, 5f, 0);
		
	}
	
	private void testNumber(String numberStr, float expectedValue, int expectedNumDecimalPlaces) {
		
		Attribute attribute = new Attribute(numberStr, fakeVO);
		
		assertEquals(1, attribute.getNChunks());
		
		byte[] data = attribute.getData();
		
		testNumberChunkCorrect(data, 0, expectedValue, expectedNumDecimalPlaces);
	}
	

	@Test public void testParseShortCommentWithRTF() {
		// The parsing is counting the "}" and not the "{" so it blows up at the end.
		// Since I am not sure exactly what is supposed to happen here I'm going to leave this for a 
		// bit.
		String attributeText = 
			"\\iAgraulus\\i0 P. Beauv., \\iAgrestis\\i0 Bub., \\iAnomalotis\\i0\n"+
			"Steud., \\iBromidium\\i0 Nees, \\iCandollea\\i0 Steud.,\n"+
			"\\iChaetotropis\\i0 Kunth, \\iDecandolea\\i0 Batard, \\iDidymochaeta\\i0\n"+
			"Steud., \\iLachnagrostis\\i0 Trin., \\iNeoschischkinia\\i0 Tsvelev,\n"+
			"\\iNotonema\\i0 Raf., \\iPentatherum\\i0 Nabelek, \\iPodagrostis\\i0\n"+
			"(Griseb.) Scribn., \\iSenisetum\\i0 Koidz., \\iTrichodium\\i0 Michaux,\n"+
			"\\iVilfa\\i0 Adans.";
		testShortTextAttribute(attributeText);
	}
	
	/**
	 * Tests the parsing of a text attribute containing only a comment.
	 * {@link au.org.ala.delta.editor.slotfile.Attribute#parse(java.lang.String, au.org.ala.delta.editor.slotfile.VOCharBaseDesc, boolean)}
	 * .
	 */
	@Test
	public void testParseShortCommentOnlyAttribute() {

		String attributeText = "Just a comment with no RTF";
		testShortTextAttribute(attributeText);
	}

	/**
	 * Tests the parsing of a text attribute containing only a comment.
	 * {@link au.org.ala.delta.editor.slotfile.Attribute#parse(java.lang.String, au.org.ala.delta.editor.slotfile.VOCharBaseDesc, boolean)}
	 * .
	 */
	@Test
	public void testParseShortNestedCommentAttribute() {

		String attributeTextWithNestedComment = "Just a comment with no RTF<Now Nested>";
		testShortTextAttribute(attributeTextWithNestedComment);
	}

	private void testShortTextAttribute(String attributeText) {
		fakeVO = setupExpectationsForTextCharacter();

		Attribute attribute = new Attribute("<"+attributeText+">", fakeVO);
		
		assertEquals(1, attribute.getNChunks());
		
		byte[] data = attribute.getData();
		
		testShortTextChunkCorrect(data, 0, attributeText);
	}
	
	private int testShortTextChunkCorrect(byte[] data, int offset, String expectedValue) {
		
		assertEquals("chunk type", (byte)ChunkType.CHUNK_TEXT, data[offset]);
		offset++;
		
		// We only have ASCII characters so string length should be the same as byte length
		// Also we are little endian so the least significant byte will be first in the array
		ByteBuffer buffer = ByteBuffer.wrap(data, offset, 2) ;
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		assertEquals("chunk length", expectedValue.length(), buffer.getShort());
		offset += 2;
		
		byte[] subData = Arrays.copyOfRange(data, offset, offset+expectedValue.length());
		assertEquals("attribute text", expectedValue, new String(subData));
		offset += expectedValue.length();
		
		return offset;
	}
	
	
	private int testNumericChunkCorrect(byte[] data, int offset, int expectedType, float expectedValue, int expectedDecimalPlaces) {
		assertEquals(expectedType, data[offset]);
		offset++;
		DeltaNumber deltaNumber = new DeltaNumber();
		deltaNumber.fromBinary(data, offset);
		assertEquals(expectedValue, deltaNumber.asFloat(), 0.001f);
		assertEquals(expectedDecimalPlaces, deltaNumber.getDecimal());
		offset += DeltaNumber.size();
		return offset;
	}
	
	private int testNumberChunkCorrect(byte[] data, int offset, float expectedValue, int expectedDecimalPlaces) {
		return testNumericChunkCorrect(data, offset, ChunkType.CHUNK_NUMBER, expectedValue, expectedDecimalPlaces);
	}
	
	private VOCharBaseDesc setupExpectationsForTextCharacter() {
		return setupExpectationsForCharacter(CharType.TEXT);
	}
	private VOCharBaseDesc setupExpectationsForIntegerCharacter() {
		return setupExpectationsForCharacter(CharType.INTEGER);
	}
	private VOCharBaseDesc setupExpectationsForRealCharacter() {
		return setupExpectationsForCharacter(CharType.REAL);
	}
	private VOCharBaseDesc setupExpectationsForMultiStateCharacter(int charType, int numStates) {
		return setupExpectationsForCharacter(charType, numStates);
	}
	
	
	private VOCharBaseDesc setupExpectationsForCharacter(int charType) {
		return setupExpectationsForCharacter(charType, 1);
	}
	
	private VOCharBaseDesc setupExpectationsForCharacter(int charType, int numStates) {
		fakeVO = context.mock(VOCharBaseDesc.class);
		final int finalCharType = charType;
		final int finalNumStates = numStates;
		
		context.checking(new Expectations() {
			{
				atLeast(1).of(fakeVO).getUniId(); will(returnValue(1));
				atLeast(1).of(fakeVO).getCharType(); will(returnValue(finalCharType));
				atLeast(1).of(fakeVO).testCharFlag((byte)1); will(returnValue(false));
				
				for (int i=1; i<=finalNumStates; i++) {
					atLeast(1).of(fakeVO).uniIdFromStateNo(i); will(returnValue(i));
					atLeast(0).of(fakeVO).stateNoFromUniId(i); will(returnValue(i));
				}
			}
		});
		return fakeVO;
	}
	
	/**
	 * Tests the isStateEncoded method of attribute.
	 */
	@Test public void testIsStateEncodedUnorderedMultistateCharWithRange() {
		
		boolean[] expectedValues = {true, false, true, false, false};
		testMultiStateChar(CharType.UNORDERED, "1-3", expectedValues); 
	}
	
	/**
	 * Tests the isStateEncoded method of attribute.
	 */
	@Test public void testIsStateEncodedUnorderedMultistateCharWithAnd() {
		
		boolean[] expectedValues = {false, true, false, true, true};
		testMultiStateChar(CharType.UNORDERED, "2&4&5", expectedValues); 
	}
	/**
	 * Tests the isStateEncoded method of attribute.
	 */
	@Test public void testIsStateEncodedOrderedMultistateCharWithRange() {
		
		boolean[] expectedValues = {true, true, true, false, false};
		testMultiStateChar(CharType.ORDERED, "1-3", expectedValues); 
	}
	
	/**
	 * Tests the isStateEncoded method of attribute.
	 */
	@Test public void testIsStateEncodedOrderedMultistateCharWithRangeAndExtraValue() {
		
		boolean[] expectedValues = {true, true, true, false, true};
		testMultiStateChar(CharType.ORDERED, "1-3&5", expectedValues); 
	}
	
	
	private void testMultiStateChar(int charType, String attributeValue, boolean[] expectedValues) {
		VOCharBaseDesc fakeVO = setupExpectationsForMultiStateCharacter(charType, 5);
		Attribute attribute = new Attribute(attributeValue, fakeVO); 
		
		int[] states = {1,2,3,4,5};
		
		checkStateEncoding(attribute, states, expectedValues);
	}
	
	private void checkStateEncoding(Attribute attribute, int[] states, boolean expectedValues[]) {
		
		for (int i=0; i<states.length; i++) {
			boolean stateEncoded = attribute.encodesState(fakeVO, states[i], true, false);
			assertEquals(expectedValues[i], stateEncoded);
		}
	}
	
	@Test public void testIsSimpleText() {
		fakeVO = setupExpectationsForTextCharacter();
		Attribute attribute = new Attribute("<simple text>", fakeVO);
		assertTrue(attribute.isSimple(fakeVO));
		
		
	}
	
	@Test public void testIsSimpleTextWithRTF() {
		fakeVO = setupExpectationsForTextCharacter();
		Attribute attribute = new Attribute("<\\b Text with RTF markup\\b0>", fakeVO);
		assertFalse(attribute.isSimple(fakeVO));
	}
	
	@Test public void testIsSimpleNumber() {
		fakeVO = setupExpectationsForIntegerCharacter();
		Attribute attribute = new Attribute("2-3", fakeVO);
		assertTrue(attribute.isSimple(fakeVO));
	}
	
	@Test public void testIsSimpleNumberWithComments() {
		fakeVO = setupExpectationsForIntegerCharacter();
		Attribute attribute = new Attribute("2<not simple anymore>-3", fakeVO);
		assertFalse(attribute.isSimple(fakeVO));
	}
	
	@Test public void testIsSimpleMultiState() {
		fakeVO = setupExpectationsForMultiStateCharacter(CharType.ORDERED, 5);
		Attribute attribute = new Attribute("2/3/5", fakeVO);
		assertTrue(attribute.isSimple(fakeVO));
	}
	
	@Test public void testIsSimpleMultiStateWithRange() {
		fakeVO = setupExpectationsForMultiStateCharacter(CharType.ORDERED, 5);
		Attribute attribute = new Attribute("2-5", fakeVO);
		assertFalse(attribute.isSimple(fakeVO));
	}
}
