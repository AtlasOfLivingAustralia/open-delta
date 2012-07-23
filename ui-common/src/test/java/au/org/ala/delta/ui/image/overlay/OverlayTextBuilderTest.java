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
package au.org.ala.delta.ui.image.overlay;

import junit.framework.TestCase;

import org.jdesktop.application.ResourceMap;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.model.image.OverlayType;

/**
 * Tests the OverlayTextBuilder class.
 */
public class OverlayTextBuilderTest extends TestCase {

	private OverlayTextBuilder _builder;
	private ImageOverlay _overlay;
	
	class ItemStub extends Item {
		private String _description;
		private int _number;
		public ItemStub(int itemNumber, String description) {
			_number = itemNumber;
			_description = description;
		}
		
		@Override
		public String getDescription() {
			return _description;
		}
		
		@Override
		public boolean isVariant() {
			return false;
		}
		
		@Override
		public int getItemNumber() {
			return  _number;
		}
		
		@Override
		public void setItemNumber(int number) {
			_number = number;
		}
		
	}
	
	class MultiStateCharacterStub extends MultiStateCharacter {
		private String _description;
		private int _number;
		
		public MultiStateCharacterStub(int number, String description) {
			super(CharacterType.Text);
			_description = description;
			_number = number;
		}
		
		@Override
		public String getDescription() {
			return _description;
		}
		
		@Override
		public String getState(int stateNum) {
			return _description;
		}
		
		@Override
		public int getCharacterId() {
			return _number;
		}
		
		@Override
		public void setCharacterNumber(int number) {
			_number = number;
		}
	}
	
	class NumericCharacterStub<T extends Number> extends NumericCharacter<T> {
		private String _units;
		private int _number;
		
		public NumericCharacterStub(int number, String units) {
			super(CharacterType.IntegerNumeric);
			_units = units;
			_number = number;
		}
		
		@Override
		public String getDescription() {
			return _units;
		}
		
		@Override
		public String getUnits() {
			return _units;
		}
		
		@Override
		public int getCharacterId() {
			return _number;
		}
		
		@Override
		public void setCharacterNumber(int number) {
			_number = number;
		}
		
	}
	
	@Before
	protected void setUp() {
		ResourceMap resources = new ResourceMap(null, getClass().getClassLoader(), "au.org.ala.delta.ui.resources.DeltaSingleFrameApplication");
		ImageSettings imageSettings = new ImageSettings();
		
		_builder = new OverlayTextBuilder(resources, imageSettings);	
		_overlay = new ImageOverlay();
		_overlay.addLocation(new OverlayLocation());
	}
	
	@Test
	public void testTextOverlay() {
		
		_overlay.overlayText = "Test text overlay";
		_overlay.type = OverlayType.OLTEXT;
		String text = _builder.getText(_overlay, null);
		
		assertEquals(_overlay.overlayText, text);
	}
	
	@Test
	public void testItemOverlay() {
		
		_overlay.overlayText = "Additional text";
		_overlay.type = OverlayType.OLITEM;
		Item item = new ItemStub(1, "Item description <Comment>");
		
		// Defaults. (no comments, use item description).
		String text = _builder.getText(_overlay, item);
		assertEquals("Item description Additional text", text);
		
		// Include comments
		_overlay.getLocation(0).flags += ImageOverlay.OL_INCLUDE_COMMENTS;
		text = _builder.getText(_overlay, item);
		assertEquals("Item description <Comment> Additional text", text);
		
		// Omit feature
		_overlay.getLocation(0).flags += ImageOverlay.OL_OMIT_DESCRIPTION;
		text = _builder.getText(_overlay, item);
		assertEquals("Additional text", text);
		
	}
	
	@Test
	public void testFeatureOverlay() {
		_overlay.overlayText = "Additional text";
		_overlay.type = OverlayType.OLFEATURE;
		MultiStateCharacterStub character = new MultiStateCharacterStub(1, "char description <Comment>");
		character.setCharacterNumber(1);
		
		// Defaults. (no comments, use item description).
		String text = _builder.getText(_overlay, character);
		assertEquals("Char description Additional text", text);
		
		// Include comments
		_overlay.getLocation(0).flags += ImageOverlay.OL_INCLUDE_COMMENTS;
		text = _builder.getText(_overlay, character);
		assertEquals("Char description <Comment> Additional text", text);
		
		// Omit feature
		_overlay.getLocation(0).flags -= ImageOverlay.OL_INCLUDE_COMMENTS;
		_overlay.getLocation(0).flags += ImageOverlay.OL_OMIT_DESCRIPTION;
		text = _builder.getText(_overlay, character);
		assertEquals("Additional text", text);
	}
	
	
	@Test
	public void testStateOverlay() {
		_overlay.overlayText = "Additional text";
		_overlay.type = OverlayType.OLSTATE;
		_overlay.stateId = 1;
		MultiStateCharacterStub character = new MultiStateCharacterStub(1, "state description <Comment>");
		
		// Defaults. (no comments, use item description).
		String text = _builder.getText(_overlay, character);
		assertEquals("1. state description Additional text", text);
		
		// Include comments
		_overlay.getLocation(0).flags += ImageOverlay.OL_INCLUDE_COMMENTS;
		text = _builder.getText(_overlay, character);
		assertEquals("1. state description <Comment> Additional text", text);
		
		// Omit feature
		_overlay.getLocation(0).flags -= ImageOverlay.OL_INCLUDE_COMMENTS;
		_overlay.getLocation(0).flags += ImageOverlay.OL_OMIT_DESCRIPTION;
		text = _builder.getText(_overlay, character);
		assertEquals("Additional text", text);
	}
	
	
	@Test
	public void testValueOverlay() {
		_overlay.overlayText = "Additional text";
		_overlay.type = OverlayType.OLVALUE;
		_overlay.minVal="1";
		_overlay.maxVal="3";
		NumericCharacterStub<Float> character = new NumericCharacterStub<Float>(1, "units <with comment>");
		
		// Defaults. (no comments, use item description).
		String text = _builder.getText(_overlay, character);
		assertEquals("1-3 units Additional text", text);
		
		// Include comments
		_overlay.getLocation(0).flags += ImageOverlay.OL_INCLUDE_COMMENTS;
		text = _builder.getText(_overlay, character);
		assertEquals("1-3 units <with comment> Additional text", text);
		
		// Omit feature
		_overlay.getLocation(0).flags -= ImageOverlay.OL_INCLUDE_COMMENTS;
		_overlay.getLocation(0).flags += ImageOverlay.OL_OMIT_DESCRIPTION;
		text = _builder.getText(_overlay, character);
		assertEquals("Additional text", text);
	}
}
