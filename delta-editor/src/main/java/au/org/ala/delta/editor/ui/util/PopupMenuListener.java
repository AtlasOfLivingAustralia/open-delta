package au.org.ala.delta.editor.ui.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Convenience class to display popup menus in a platform dependent way.
 * Taken from the Swing tutorial with minor changes.
 *
 */
public class PopupMenuListener extends MouseAdapter {
	
	private JPopupMenu _popup;
	
	public PopupMenuListener(JPopupMenu popup, JComponent component) {
		_popup = popup;
		component.addMouseListener(this);
	}
	
	public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            _popup.show(e.getComponent(),
                       e.getX(), e.getY());
        }
    }
}
