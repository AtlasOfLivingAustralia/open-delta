package au.org.ala.delta.ui.image;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
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

	private static final String IMAGE_OVERLAY_PROPERTY = "ImageOverlay";

	private static final long serialVersionUID = -6735023009826819178L;
	
	/** The Image we are displaying */
	protected Image _image;
	
	/** Creates image overlays */
	private OverlayComponentFactory _factory;
	
	protected List<ImageOverlay> _overlays;
	
	protected List<JComponent> _components;
	
	private List<OverlaySelectionObserver> _observers;
	
	/** Kept for convenience when toggling the display of hotspots */
	private List<HotSpotGroup> _hotSpotGroups;
	
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
		_hotSpotGroups = new ArrayList<HotSpotGroup>();
		for (ImageOverlay overlay : _overlays) {
			JComponent overlayComp = _factory.createOverlayComponent(overlay, subject);
			
			if (overlayComp == null) {
				continue;
			}
			overlayComp.putClientProperty(IMAGE_OVERLAY_PROPERTY, overlay);
			add(overlayComp, overlay.getLocation(0));
			
			if (overlayComp instanceof OverlayButton) {
				
				((OverlayButton)overlayComp).addActionListener(this);
			}
			
			if (overlayComp instanceof SelectableTextOverlay) {
				// If the overlay has associated hotspots, add them also.
				int hotSpotCount = overlay.getNHotSpots();
				if (hotSpotCount > 0) {
					HotSpotGroup group = new HotSpotGroup((SelectableTextOverlay)overlayComp);
					_hotSpotGroups.add(group);
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
	
	public void setDisplayHotSpots(boolean displayHotSpots) {
		for (HotSpotGroup group : _hotSpotGroups) {
			group.setDisplayHotSpots(displayHotSpots);
		}
		repaint();
	}
	
	public void setDisplayTextOverlays(boolean displayText) {
		for (JComponent overlayComp : _components) {
			if (isTextOverlay(overlayComp)) {
				overlayComp.setVisible(displayText);
			}
		}
	}
	
	private boolean isTextOverlay(JComponent overlayComp) {
		boolean isText = false;
		ImageOverlay overlay = (ImageOverlay)overlayComp.getClientProperty(IMAGE_OVERLAY_PROPERTY);
		if (overlay != null) {
			isText = OverlayType.isTextOverlay(overlay);
		}
		return isText;
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
		ImageOverlay overlay = (ImageOverlay)comp.getClientProperty(IMAGE_OVERLAY_PROPERTY);
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
