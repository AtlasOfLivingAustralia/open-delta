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
