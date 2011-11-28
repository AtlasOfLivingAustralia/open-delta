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
package au.org.ala.delta.ui.rtf;

import java.awt.event.KeyEvent;

public class CircumflexCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public CircumflexCharacterHandler() {
		
		super(KeyEvent.VK_6, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.Circumflex, 0x5E,
			    pair('a', 0xe2),
			    pair('A', 0xc2),
			    pair('e', 0xea),
			    pair('E', 0xca),
			    pair('i', 0xee),
			    pair('I', 0xce),
			    pair('o', 0xf4),
			    pair('O', 0xd4),
			    pair('u', 0xfb),
			    pair('U', 0xdb)				
		);
		
	}

}
