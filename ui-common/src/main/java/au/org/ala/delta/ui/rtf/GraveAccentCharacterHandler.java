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

public class GraveAccentCharacterHandler extends SpecialCharHandler {

	@SuppressWarnings("unchecked")
	public GraveAccentCharacterHandler() {
		super(KeyEvent.VK_BACK_QUOTE, KeyEvent.CTRL_MASK, SpecialCharacterMode.GraveAccents, 0x60,
		    pair('a', 0xe0),
		    pair('A', 0xc0),
		    pair('e', 0xe8),
		    pair('E', 0xc8),
		    pair('i', 0xec),
		    pair('I', 0xcc),
		    pair('o', 0xf2),
		    pair('O', 0xd2),
		    pair('u', 0xf9),
		    pair('U', 0xd9),
		    pair('`', 145),
		    pair('\"', 147),
		    pair('>', 187),
		    pair('<', 171)
		);
	}

}
