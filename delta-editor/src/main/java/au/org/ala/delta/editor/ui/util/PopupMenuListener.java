package au.org.ala.delta.editor.ui.util;

import java.awt.Point;
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
	protected JComponent _component;
	
	public PopupMenuListener(JPopupMenu popup, JComponent component) {
		_popup = popup;
		_component = component;
		component.addMouseListener(this);
	}
	
	public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    protected void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	showPopup(new Point(e.getX(), e.getY()));
        }
    }
    
    protected JPopupMenu getPopup() {
    	return _popup;
    }
    
    protected void showPopup(Point p) {
    	getPopup().show(_component, p.x, p.y);
    }
}
