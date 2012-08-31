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

public class DoubleAcuteCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public DoubleAcuteCharacterHandler() {
		super(KeyEvent.VK_QUOTE, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.DoubleAcute, 0x2DD,
		    pair('o', 0x151),
		    pair('O', 0x150),
		    pair('u', 0x16e),
		    pair('U', 0x16f)				
		);
	}

}
