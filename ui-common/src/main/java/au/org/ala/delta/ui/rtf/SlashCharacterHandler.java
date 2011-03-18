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
