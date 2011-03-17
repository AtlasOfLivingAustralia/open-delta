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
