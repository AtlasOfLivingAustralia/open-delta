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

public class SlashCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public SlashCharacterHandler() {
		super(KeyEvent.VK_SLASH, KeyEvent.CTRL_MASK, SpecialCharacterMode.Slash, 0x2044,
		    pair('c', 0xa2),
		    pair('l', 0x142),
		    pair('L', 0x141),
		    pair('o', 0xf8),
		    pair('O', 0xd8)
		);
	}

}
