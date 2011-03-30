package au.org.ala.delta.editor.support;

import java.awt.Dimension;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

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
		getContext().getSessionStorage().putProperty(JInternalFrame.class, new InternalFrameProperty());
	}

	protected void show(JInternalFrame frame) {
		assert (frame != null);

		if (_desktop == null) {
			createDesktop();
		}
		if (!_desktop.isVisible()) {
			show(_desktop);
		}

		frame.addInternalFrameListener(new FrameListener());
		addToDesktop(frame);

	}

	private void addToDesktop(JInternalFrame frame) {
		frame.setFrameIcon(EditorUIUtils.createDeltaImageIcon());
		_desktop.add(frame);
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

		// Restore session state
		restoreSession(frame);

		frame.setVisible(true);
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
				configureDefaultPosition(frame);
			}
		} else {
			configureDefaultPosition(frame);
		}
	}

	private void configureDefaultPosition(JInternalFrame frame) {
		try {
			frame.setMaximum(false);
		} catch (Exception ex) {
			// ignore
		}
		frame.setSize(new Dimension(800, 500));
	}

	class FrameListener extends InternalFrameAdapter {

		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			if (e.getSource() instanceof JInternalFrame) {
				saveSession((JInternalFrame) e.getSource());
			}
		}

	}

}
