package au.org.ala.delta.ui.rtf;

import java.awt.event.KeyEvent;

public class DoubleAcuteCharacterHandler extends SpecialCharHandler {
	
	@SuppressWarnings("unchecked")
	public DoubleAcuteCharacterHandler() {
		super(KeyEvent.VK_QUOTE, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK, SpecialCharacterMode.DoubleAcute, 0x2DD,
		    pair('o', 0x151),
		    pair('O', 0x150),
		    pair('u', 0x16e),
		    pair('U', 0x16f)				
		);
	}

}
