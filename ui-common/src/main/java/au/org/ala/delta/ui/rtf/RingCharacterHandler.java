package au.org.ala.delta.ui.rtf;

import java.awt.event.KeyEvent;

public class RingCharacterHandler extends SpecialCharHandler {

	@SuppressWarnings("unchecked")
	public RingCharacterHandler() {
		super(KeyEvent.VK_2, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.Ring, 0xb0,
		    pair('a', 0xe5),
		    pair('A', 0xc5),
		    pair('u', 0x16e),
		    pair('U', 0x16f)				
	    );
	}
}
