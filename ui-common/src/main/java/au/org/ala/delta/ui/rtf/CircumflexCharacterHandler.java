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
