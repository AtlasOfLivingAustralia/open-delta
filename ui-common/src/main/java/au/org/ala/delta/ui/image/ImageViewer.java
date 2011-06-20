package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.overlay.OverlayLocation;

/**
 * Displays a single DELTA Image.
 */
public class ImageViewer extends ImagePanel implements LayoutManager2 {

	private static final long serialVersionUID = -6735023009826819178L;
	
	/** The Image we are displaying */
	private Image _image;
	
	/** The owner (Character or Item) of the image we are displaying */
	private Illustratable _illustratable;
	
	/** Creates image overlays */
	private OverlayComponentFactory _factory;
	
	private List<ImageOverlay> _overlays;
	
	private Map<JComponent, au.org.ala.delta.model.image.OverlayLocation> _overlayComponents;
	
	private List<JComponent> _components;
	
	/**
	 * Creates a new ImageViewer for the supplied Image.
	 * @param imagePath the path to find relative images on.
	 * @param image the image to view.
	 */
	public ImageViewer(String imagePath, Image image, DeltaDataSet dataSet, Illustratable imageOwner) {
		_image = image;
		_illustratable = imageOwner;
		
		ResourceMap resources = Application.getInstance().getContext().getResourceMap();
		_factory = new OverlayComponentFactory(dataSet, resources);
		setLayout(this);
		displayImage(image.getImageLocation(imagePath));
		_overlayComponents = new HashMap<JComponent, au.org.ala.delta.model.image.OverlayLocation>();
		_components = new ArrayList<JComponent>();
		addOverlays();
	}
	
	private void addOverlays() {
		_overlays = _image.getOverlays();
		for (ImageOverlay overlay : _overlays) {
			JComponent overlayComp = _factory.createOverlayComponent(overlay, _illustratable);
			if (overlayComp == null) {
				continue;
			}
			add(overlayComp, overlay.getLocation(0));
			
			// If the overlay has associated hotspots, add them also.
			for (int i=1; i<=overlay.getNHotSpots(); i++) {
				overlay.getLocation(i);
				add(_factory.createHotSpot(overlay, i), overlay.getLocation(i));
			}
			
		}
	}
	
	/**
	 * Lays out the Image overlays in this container.
	 */
	private void layoutOverlays() {
		 
	
		for (JComponent overlayComp : _components) {
			
			au.org.ala.delta.model.image.OverlayLocation overlay = _overlayComponents.get(overlayComp);
			OverlayLocation location = new OverlayLocation(this, overlayComp, overlay);
			
			Rectangle bounds = new Rectangle(location.getX(), location.getY(), 
					location.preferredWidth(), location.preferredHeight()); 
			overlayComp.setBounds(bounds);
		}
		
	}

	
	/**
	 * Returns a new ImageViewer housed in a JDialog.
	 * @param parent the parent Window for the dialog.
	 * @param imagePath the path to find relative images on.
	 * @param image the image to view.
	 * @return an instance of JDialog containing the ImageViewer.
	 */
	public static JDialog asDialog(Window parent, String imagePath, Image image, DeltaDataSet dataSet, Illustratable imageOwner) {
		JDialog dialog = new JDialog(parent);
		ImageViewer viewer = new ImageViewer(imagePath, image, dataSet, imageOwner);

		dialog.getContentPane().add(viewer, BorderLayout.CENTER);
		dialog.setSize(viewer.getPreferredSize());
		
		return dialog;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {}
	
	

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints == null) {
			throw new IllegalArgumentException("Cannot use null constraints");
		}
		_components.add((JComponent)comp);
		_overlayComponents.put((JComponent)comp, (au.org.ala.delta.model.image.OverlayLocation)constraints);
		
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(2000,2000);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		_components.remove((JComponent)comp);
		_overlayComponents.remove((JComponent)comp);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension d = new Dimension();
		d.width = getPreferredImageWidth();
		d.height = getPreferredImageHeight();
		
		return d;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(100,100);
	}

	@Override
	public void layoutContainer(Container parent) {
		layoutOverlays();
	}
	
	
	
}
