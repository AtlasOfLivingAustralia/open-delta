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

public class GreekCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public GreekCharacterHandler() {
		super(KeyEvent.VK_G, KeyEvent.CTRL_MASK, SpecialCharacterMode.Greek, 'g',
		    pair('a', 0x3b1),
		    pair('A', 0x391),
		    pair('b', 0x3b2),
		    pair('B', 0x392),
		    pair('c', 0x3c8),
		    pair('C', 0x3a8),
		    pair('d', 0x3b4),
		    pair('D', 0x394),
		    pair('e', 0x3b5),
		    pair('E', 0x395),
		    pair('f', 0x3c6),
		    pair('F', 0x3a6),
		    pair('g', 0x3b3),
		    pair('G', 0x393),
		    pair('h', 0x3b7),
		    pair('H', 0x397),
		    pair('i', 0x3b9),
		    pair('I', 0x399),
		    pair('j', 0x3be),
		    pair('J', 0x39e),
		    pair('k', 0x3ba),
		    pair('K', 0x39a),
		    pair('l', 0x3bb),
		    pair('L', 0x39b),
		    pair('m', 0x3bc),
		    pair('M', 0x39c),
		    pair('n', 0x3bd),
		    pair('N', 0x39d),
		    pair('o', 0x3bf),
		    pair('O', 0x39f),
		    pair('p', 0x3c0),
		    pair('P', 0x3a0),
		    pair('r', 0x3c1),
		    pair('R', 0x3a1),
		    pair('s', 0x3c3),
		    pair('S', 0x3a3),
		    pair('t', 0x3c4),
		    pair('T', 0x3a4),
		    pair('u', 0x3b8),
		    pair('U', 0x398),
		    pair('v', 0x3c9),
		    pair('V', 0x3a9),
		    pair('w', 0x3c2),
		    pair('W', 0x3a3),
		    pair('x', 0x3c7),
		    pair('X', 0x3a7),
		    pair('y', 0x3c5),
		    pair('Y', 0x3a5),
		    pair('z', 0x3b6),
		    pair('Z', 0x396)
		);
	}

}
