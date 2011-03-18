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
