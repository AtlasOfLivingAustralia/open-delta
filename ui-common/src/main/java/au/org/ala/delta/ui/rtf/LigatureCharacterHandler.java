package au.org.ala.delta.ui.rtf;

import java.awt.event.KeyEvent;

public class LigatureCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public LigatureCharacterHandler() {
		super(KeyEvent.VK_7, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.Ligature, (int) '&',
		    pair('a', 0xe6),
		    pair('A', 0xc6),
		    pair('o', 0x153),
		    pair('O', 0x152),
		    pair('s', 0xdf)	
		);
	}

}
