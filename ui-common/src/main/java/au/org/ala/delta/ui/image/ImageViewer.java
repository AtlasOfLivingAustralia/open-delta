package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;

/**
 * Displays a single DELTA Image.
 */
public class ImageViewer extends ImagePanel {

	private static final long serialVersionUID = -6735023009826819178L;
	
	/** The Image we are displaying */
	private Image _image;
	
	/**
	 * Creates a new ImageViewer for the supplied Image.
	 * @param imagePath the path to find relative images on.
	 * @param image the image to view.
	 */
	public ImageViewer(String imagePath, Image image) {
		_image = image;
		setLayout(null);
		displayImage(image.getImageLocation(imagePath));
		layoutOverlays();
	}
	
	/**
	 * Lays out the Image overlays in this container.
	 */
	private void layoutOverlays() {
		List<ImageOverlay> overlays = _image.getOverlays();
		
		for (ImageOverlay overlay : overlays) {
			JComponent overlayComp = OverlayComponentFactory.createOverlayComponent(overlay);
			if (overlayComp == null) {
				continue;
			}
			OverlayLocation loc = overlay.location.get(0);
			Rectangle bounds = new Rectangle(loc.X, loc.Y, 
					overlayComp.getPreferredSize().width, overlayComp.getPreferredSize().height); 
			overlayComp.setBounds(bounds);
			add(overlayComp);
		}
		
	}
	
	
	/**
	 * Returns a new ImageViewer housed in a JDialog.
	 * @param parent the parent Window for the dialog.
	 * @param imagePath the path to find relative images on.
	 * @param image the image to view.
	 * @return an instance of JDialog containing the ImageViewer.
	 */
	public static JDialog asDialog(Window parent, String imagePath, Image image) {
		JDialog dialog = new JDialog(parent);
		ImageViewer viewer = new ImageViewer(imagePath, image);

		dialog.getContentPane().add(viewer, BorderLayout.CENTER);
		dialog.setSize(viewer.getPreferredSize());
		
		return dialog;
	}
	
}
