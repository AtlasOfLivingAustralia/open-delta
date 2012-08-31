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

public class CedillaCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public CedillaCharacterHandler() {
		super(KeyEvent.VK_COMMA, KeyEvent.CTRL_MASK, SpecialCharacterMode.Cedilla, 0xB8,
		    pair('c', 0xe7),
		    pair('C', 0xc7),
		    pair('s', 0x15f),
		    pair('S', 0x15e),
		    pair('k', 0x137),
		    pair('K', 0x136),
		    pair('l', 0x13c),
		    pair('L', 0x13b),
		    pair('n', 0x146),
		    pair('N', 0x145),
		    pair('r', 0x157),
		    pair('R', 0x156),
		    pair('t', 0x163),
		    pair('T', 0x162)
		);
	}

}
