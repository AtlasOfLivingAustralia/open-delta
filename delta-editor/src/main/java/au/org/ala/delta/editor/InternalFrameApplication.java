package au.org.ala.delta.editor;

import java.awt.Dimension;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;

/**
 * Extends the SingleFrameApplication to include support for InternalFrames.
 */
public abstract class InternalFrameApplication extends DeltaSingleFrameApplication {

	/** The desktop we are working with */
	protected JDesktopPane _desktop;
	
	/**
	 * Initialises the desktop.
	 */
	protected void createDesktop() {
		_desktop = new JDesktopPane();
		_desktop.setBackground(SystemColor.control);
	}

	
	protected void show(JInternalFrame frame) {
		assert (frame != null);

		if (_desktop == null) {
			createDesktop();
		}
		if (!_desktop.isVisible()) {
			show(_desktop);
		}
		
		addToDesktop(frame);
		
	}

	private void addToDesktop(JInternalFrame frame) {
		frame.setFrameIcon(EditorUIUtils.createDeltaImageIcon());
		_desktop.add(frame);
		frame.setClosable(true);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setIconifiable(true);
		frame.setVisible(true);
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

		try {
			frame.setMaximum(false);
		} catch (Exception ex) {
			// ignore
		}
		frame.setSize(new Dimension(800, 500));
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


}
