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

public class HacekCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public HacekCharacterHandler() {
		super(KeyEvent.VK_6, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK, SpecialCharacterMode.Hacek, 0x2c7,
		    pair('e', 0x11b),
		    pair('E', 0x11a),
		    pair('c', 0x10d),
		    pair('C', 0x10c),
		    pair('d', 0x10f),
		    pair('D', 0x10e),
		    pair('l', 0x13e),
		    pair('L', 0x13d),
		    pair('n', 0x148),
		    pair('N', 0x147),
		    pair('r', 0x159),
		    pair('R', 0x158),
		    pair('s', 0x161),
		    pair('S', 0x160),
		    pair('t', 0x165),
		    pair('T', 0x164),
		    pair('z', 0x17e),
		    pair('Z', 0x17d)				
		);
	}

}
