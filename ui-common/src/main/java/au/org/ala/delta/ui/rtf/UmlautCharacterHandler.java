package au.org.ala.delta.ui.rtf;

import java.awt.event.KeyEvent;

public class UmlautCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public UmlautCharacterHandler() {
		super(KeyEvent.VK_SEMICOLON, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.Umlaut, 0xA8,
		    pair('a', 0xe0),
		    pair('a', 0xe4),
		    pair('A', 0xc4),
		    pair('e', 0xeb),
		    pair('E', 0xcb),
		    pair('i', 0xef),
		    pair('I', 0xcf),
		    pair('o', 0xf6),
		    pair('O', 0xd6),
		    pair('u', 0xfc),
		    pair('U', 0xdc),
		    pair('y', 0xff),
		    pair('Y', 159)		    
	    );
	}

}
