package au.org.ala.delta.ui.image;

import javax.swing.JComponent;
import javax.swing.JLabel;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;

/** 
 * The OverlayComponentFactory is responsible for creating components appropriate to
 * display the various types of ImageOverlay.
 */
public class OverlayComponentFactory {

	public static JComponent createOverlayComponent(ImageOverlay overlay) {
		
		JComponent component = null;
		if (overlay.isType(OverlayType.OLTEXT)) {
			component = new JLabel(overlay.overlayText);
			
		}
		else {
			System.out.println("Unsupported overlay type: "+overlay.type);
		}
		if (component != null) {
			component.setOpaque(false);
		}
		return component;
	}
}
