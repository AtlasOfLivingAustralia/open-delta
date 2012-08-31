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
package au.org.ala.delta.translation;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.MultiStateCharacter;

import junit.framework.TestCase;

/**
 * Tests the formatter class.
 */
public class FormatterTest extends TestCase {

	private DeltaDataSetFactory _factory = new DefaultDataSetFactory();
	private Formatter _formatter = new Formatter(new DeltaContext());
	
	@Test
	public void testStripCommentsInputWithNoComments() {
		String textWithNoComments = "This has no comments";
		
		assertEquals(textWithNoComments, _formatter.stripComments(textWithNoComments));
	}
	
	@Test
	public void testStripCommentsSingleComment() {
		String textWithComment = "This has <one> comment in the middle";
		
		assertEquals("This has comment in the middle", _formatter.stripComments(textWithComment));
	
		textWithComment = "<This> has one comment at the start";
		assertEquals("has one comment at the start", _formatter.stripComments(textWithComment));
		
		textWithComment = "This has one comment at the <end>";
		assertEquals("This has one comment at the", _formatter.stripComments(textWithComment));
	}
	
	@Test
	public void testStripCommentsMultipleComments() {
		String textWithComment = "This has <more> than <one> comment.";
		
		assertEquals("This has than comment.", _formatter.stripComments(textWithComment));
	}
	
	@Test
	public void testFormatMultiStateCharacter() {
		String attribute = "1-2/2&3";
		MultiStateCharacter character = (MultiStateCharacter)_factory.createCharacter(CharacterType.UnorderedMultiState, 1);
		character.setNumberOfStates(3);
		character.setState(1, "state 1");
		character.setState(2, "state 2");
		character.setState(3, "state 3");
		
		String result = _formatter.formatAttribute(character, attribute);
		assertEquals("state 1 to state 2, or state 2 and state 3", result);
	}
	
	
}
