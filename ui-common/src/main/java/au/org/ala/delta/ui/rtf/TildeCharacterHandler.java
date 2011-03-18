package au.org.ala.delta.ui.rtf;

import java.awt.event.KeyEvent;

public class TildeCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public TildeCharacterHandler() {
		super(KeyEvent.VK_BACK_QUOTE, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.Tilde, 0x7e,
		    pair('a', 0xe3),
		    pair('A', 0xc3),
		    pair('o', 0xf5),
		    pair('O', 0xd5),
		    pair('n', 0xf1),
		    pair('N', 0xd1)		
		);
	}

}
