package au.org.ala.delta.editor.support;

import static org.jdesktop.application.utils.SwingHelper.computeVirtualGraphicsBounds;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import org.jdesktop.application.session.WindowProperty;
import org.jdesktop.application.session.WindowState;

/**
 * Extends the WindowProperty to provide support for JInternalFrames.  Unfortunately it has a lot
 * of duplication of code due to various private methods or members.
 */
public class InternalFrameProperty extends WindowProperty {

	/**
	 * Overrides getSessionState of the parent class to handle minimise/maximise
	 * on the internal frame.
	 */
	@Override
	public Object getSessionState(Component c) {

		if (!(c instanceof JInternalFrame)) {
			return super.getSessionState(c);
		}
		
		JInternalFrame frame = (JInternalFrame)c;
		
		GraphicsConfiguration gc = c.getGraphicsConfiguration();
		Rectangle gcBounds = (gc == null) ? null : gc.getBounds();
		Rectangle frameBounds = frame.getNormalBounds();
		
		int frameState = Frame.NORMAL;
		if (frame.isMaximum()) {
			frameState = Frame.MAXIMIZED_BOTH;
		}
			
		int screenCount =  GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
		if (frameBounds.isEmpty()) {
			return null;
		}
		return new WindowState(frameBounds, gcBounds, screenCount, frameState);
	}

	/**
	 * Overrides setSessionState of the parent class to handle minimise/maximise
	 * on the internal frame.
	 */
	@Override
	public void setSessionState(Component c, Object state) {

		if (state == null) {
			return;
		}
        if ((state != null) && !(state instanceof WindowState)) {
            throw new IllegalArgumentException("invalid state");
        }
        if (c instanceof JInternalFrame) {
			WindowState windowState = (WindowState) state;
			JInternalFrame internalFrame = (JInternalFrame) c;
            
            Rectangle gcBounds0 = windowState.getGraphicsConfigurationBounds();
            if (gcBounds0 != null && internalFrame.isResizable()) {
                if (computeVirtualGraphicsBounds().contains(gcBounds0.getLocation())) {
                    internalFrame.setBounds(windowState.getBounds());
                } else {
                    internalFrame.setSize(windowState.getBounds().getSize());
                }
            }
           
			int frameState = windowState.getFrameState();
			if ((frameState & Frame.MAXIMIZED_BOTH) != 0) {
				try {
					internalFrame.setMaximum(true);
				} catch (PropertyVetoException e) {
				}
			}
		}
	}

}
