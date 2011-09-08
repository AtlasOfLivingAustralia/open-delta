package au.org.ala.delta.editor.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Listens for Enter / Shift Enter.
 */
public abstract class SelectionNavigationKeyListener implements KeyListener {
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (isSelectionNavigationKeyCombination(e)) {
			e.consume();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if (isSelectionNavigationKeyCombination(e)) {
			e.consume();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (isSelectionNavigationKeyCombination(e)) {
			e.consume();
			if (e.getModifiers() == 0) {
				advanceSelection();
			}
			else {
				reverseSelection();
			}
		}
	}
	
	protected void advanceSelection() {}
	protected void reverseSelection() {}
		
	protected boolean isSelectionNavigationKeyCombination(KeyEvent e) {
		return noModifiersOrShift(e.getModifiers()) && e.getKeyCode() == KeyEvent.VK_ENTER;
	}
		
	protected boolean noModifiersOrShift(int modifiers) {
		return ((modifiers == 0) || ((modifiers & KeyEvent.SHIFT_MASK) > 0));
	}
}
