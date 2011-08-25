package au.org.ala.delta.editor.support;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;

/**
 * Extends the SingleFrameApplication to include support for InternalFrames.
 */
public abstract class InternalFrameApplication extends DeltaSingleFrameApplication implements InternalFrameListener {

	/** The desktop we are working with */
	protected JDesktopPane _desktop;

	protected List<JInternalFrame> _frames = new ArrayList<JInternalFrame>();
	
	/**
	 * Initialises the desktop.
	 */
	protected void createDesktop() {
		_desktop = new JDesktopPane();
		_desktop.setBackground(SystemColor.control);
		getContext().getSessionStorage().putProperty(JInternalFrame.class, new InternalFrameProperty());
	}

	public void show(JInternalFrame frame) {
		assert (frame != null);

		if (_desktop == null) {
			createDesktop();
		}
		if (!_desktop.isVisible()) {
			show(_desktop);
		}
		getContext().getResourceMap().injectComponents(frame);
		_frames.add(frame);
		frame.addInternalFrameListener(this);
		addToDesktop(frame);

	}

	private void addToDesktop(JInternalFrame frame) {
		frame.setFrameIcon(EditorUIUtils.createDeltaImageIcon());
		
		frame.setClosable(true);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setIconifiable(true);

		frame.setFrameIcon(EditorUIUtils.createInternalFrameNormalIcon());
		frame.addPropertyChangeListener(JInternalFrame.IS_MAXIMUM_PROPERTY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Boolean newValue = (Boolean) evt.getNewValue();
				JInternalFrame frame = (JInternalFrame) evt.getSource();
				if (newValue) {
					frame.setFrameIcon(EditorUIUtils.createInternalFrameMaximizedIcon());
				} else {
					frame.setFrameIcon(EditorUIUtils.createInternalFrameNormalIcon());
				}
			}
		});
	
		_desktop.add(frame);
		
		// Restore session state
		restoreSession(frame);
		boolean maximum = frame.isMaximum();

		Rectangle bounds = frame.getBounds();
		if ((bounds.height == 0) || (bounds.width == 0)) {
			Dimension size = frame.getPreferredSize();
			frame.setBounds(new Rectangle(bounds.x, bounds.y, size.width, size.height));
		}

		frame.setVisible(true);
		
		// This is to work around a strange bit of code (looks like a bug) that gets invoked in the 
		// WindowsDesktopManager during setVisible that will un-maximise the frame.
		try {
			if (maximum) {
				frame.setMaximum(true);
			}
		} catch (PropertyVetoException e) {
		}
	}

	/**
	 * Tiles the open JInternalFrames.
	 */
	protected void tileFramesInDesktopPane() {

		// How many frames do we have?
		JInternalFrame[] allframes = _desktop.getAllFrames();
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = _desktop.getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (!f.isClosed()) {
					try {
						f.setMaximum(false);
						f.setIcon(false);
					} catch (PropertyVetoException ignored) {
					}
				}

				_desktop.getDesktopManager().resizeFrame(f, x, y, w, h);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
	}

	private void saveSession(JInternalFrame frame) {
		if (frame == null) {
			return;
		}
		String componentName = frame.getName();
		if (componentName == null) {
			return;
		}
		try {
			getContext().getSessionStorage().save(frame, componentName + ".session.xml");
		} catch (IOException e) {
		}

	}

	private void restoreSession(JInternalFrame frame) {
		if ((frame != null) && (frame.getName() != null)) {
			try {
				getContext().getSessionStorage().restore(frame, frame.getName() + ".session.xml");
			} catch (IOException e) {
				
			}
		} 
		if (!frame.isMaximum()) {
			offsetPosition(frame);
		}
	}

	private void offsetPosition(JInternalFrame frame) {
		
		Point p = frame.getLocation();
		int offset = 0;
		while (positionTaken(frame, p.x+offset, p.y+offset)) {
			offset += 10;
		}
		frame.setLocation(p.x+offset, p.y+offset);
	}
	
	
	private boolean positionTaken(JInternalFrame newFrame, int x, int y) {
		boolean taken = false;
		for (JInternalFrame frame : _desktop.getAllFrames()) {
			if (frame != newFrame) {
				
				if ((Math.abs(frame.getLocation().x - x) <= 10) &&
				    (Math.abs(frame.getLocation().y - y) <= 10)) {
				    taken = true;
				    break;
				}
			}
		}
		return taken;
	}


	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		_frames.remove(e.getInternalFrame());
		saveSession(e.getInternalFrame());
	}
	
	 /**
     * Invoked when an internal frame is activated.
     */
    public void internalFrameActivated(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is de-activated.
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}
}
