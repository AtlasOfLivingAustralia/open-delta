package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpot;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.ui.image.overlay.HotSpotObserver;
import au.org.ala.delta.ui.image.overlay.OverlayButton;
import au.org.ala.delta.ui.image.overlay.OverlayLocation;
import au.org.ala.delta.ui.image.overlay.OverlayLocationProvider;
import au.org.ala.delta.ui.image.overlay.SelectableTextOverlay;

/**
 * Displays a single DELTA Image.
 */
public class ImageViewer extends ImagePanel implements LayoutManager2, HotSpotObserver, ActionListener {

	private static final long serialVersionUID = -6735023009826819178L;
	
	/** The Image we are displaying */
	private Image _image;
	
	/** Creates image overlays */
	private OverlayComponentFactory _factory;
	
	private List<ImageOverlay> _overlays;
	
	private List<JComponent> _components;
	
	private List<OverlaySelectionObserver> _observers;
	
	/**
	 * Creates a new ImageViewer for the supplied Image.
	 * @param imagePath the path to find relative images on.
	 * @param image the image to view.
	 */
	public ImageViewer(String imagePath, Image image, DeltaDataSet dataSet) {
		_image = image;
		
		ResourceMap resources = Application.getInstance().getContext().getResourceMap();
		_factory = new OverlayComponentFactory(resources);
		setLayout(this);
		displayImage(image.getImageLocation(imagePath));
		_components = new ArrayList<JComponent>();
		addOverlays();
	}
	
	private void addOverlays() {
		_overlays = _image.getOverlays();
		Illustratable subject = _image.getSubject();
		for (ImageOverlay overlay : _overlays) {
			JComponent overlayComp = _factory.createOverlayComponent(overlay, subject);
			if (overlayComp == null) {
				continue;
			}
			add(overlayComp, overlay.getLocation(0));
			
			if (overlayComp instanceof OverlayButton) {
				overlayComp.putClientProperty("ImageOverlay", overlay);
				((OverlayButton)overlayComp).addActionListener(this);
			}
			
			if (overlayComp instanceof SelectableTextOverlay) {
				// If the overlay has associated hotspots, add them also.
				int hotSpotCount = overlay.getNHotSpots();
				if (hotSpotCount > 0) {
					HotSpotGroup group = new HotSpotGroup((SelectableTextOverlay)overlayComp);
					for (int i=1; i<=hotSpotCount; i++) {
						overlay.getLocation(i);
						HotSpot hotSpot = _factory.createHotSpot(overlay, i);
						group.add(hotSpot);
						add(hotSpot, overlay.getLocation(i));
					}
				}
			}
		}
	}
	
	/**
	 * Lays out the Image overlays in this container.
	 */
	private void layoutOverlays() {
		 
		for (JComponent overlayComp : _components) {
			
			OverlayLocationProvider locationProvider = (OverlayLocationProvider)overlayComp;
			
			OverlayLocation location = locationProvider.location(this);
			
			Rectangle bounds = new Rectangle(location.getX(), location.getY(), 
					location.getWidth(), location.getHeight()); 
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
		ImageViewer viewer = new ImageViewer(imagePath, image, dataSet);

		dialog.getContentPane().add(viewer, BorderLayout.CENTER);
		dialog.pack();
		
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
	
	protected void fireOverlaySelected(ImageOverlay overlay) {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).overlaySelected(overlay);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JComponent comp = (JComponent)e.getSource();
		ImageOverlay overlay = (ImageOverlay)comp.getClientProperty("ImageOverlay");
		if (overlay != null) {
			fireOverlaySelected(overlay);
		}
	}
	
	@Override
	public void hotSpotEntered(ImageOverlay overlay) {
		
	}

	@Override
	public void hotSpotExited(ImageOverlay overlay) {
		
	}

	@Override
	public void hotSpotSelected(ImageOverlay overlay) {
		fireOverlaySelected(overlay);
	}

	public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_observers.add(observer);
	}
	
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_observers.remove(observer);
	}
}
